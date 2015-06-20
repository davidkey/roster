package com.dak.duty.service;

import static com.dak.duty.repository.specification.PersonSpecs.isActive;
import static com.dak.duty.repository.specification.PersonSpecs.sameOrg;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import lombok.NonNull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.exception.InvalidIdException;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.model.comparable.EventRosterItemSortByDutyOrder;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;
import com.dak.duty.service.IntervalService.EventTypeDetailNode;
import com.dak.duty.service.container.EventCalendarNode;
import com.dak.duty.service.container.comparable.EventCalendarNodeSortByDate;

@Service
@Transactional
public class EventService {
   
   private static final Logger logger = LoggerFactory.getLogger(EventService.class);

   @Autowired
   PersonRepository personRepos;

   @Autowired 
   PersonService personService;

   @Autowired
   EventRepository eventRepos;

   @Autowired
   EventTypeRepository eventTypeRepos;

   @Autowired
   IntervalService intervalService;

   @Autowired
   DutyRepository dutyRepos;

   /**
    * Attempt to fill any empty slots in any current and future events.
    * @return number of slots filled
    */
   public int fillEmptySlots(){

      int slotsFilled = 0;
      final List<Event> allCurrentAndFutureEvents = eventRepos.findAllByDateEventGreaterThanEqual(intervalService.getCurrentSystemDate());

      for(Event event : allCurrentAndFutureEvents){
         if(!event.isRosterFullyPopulated()){
            slotsFilled += fillEmptySlots(event);
         }
      }

      return slotsFilled;
   }

   
   /**
    * Attempts to fill any empty slots in Event.
    * @param event
    * @return number of slots filled
    */
   public int fillEmptySlots(final Event event){
      return fillEmptySlots(event, new HashSet<Person>());
   }
   
   /**
    * Attempts to fill any empty slots in Event, exluding Person.
    * @param event
    * @param person : person to exclude from duty
    * @return number of slots filled
    */
   public int fillEmptySlots(final Event event, final Person person){
      final Set<Person> singlePersonSet = new HashSet<Person>(1);
      singlePersonSet.add(person);
      
      return fillEmptySlots(event, singlePersonSet);
   }

   /**
    * Attempts to fill any empty slots in Event, excluding certain people
    * @param event : Event to fill
    * @param peopleExcluded : Set of People to exclude from duty
    * @return number of slots filled
    */
   public int fillEmptySlots(@NonNull final Event event, @NonNull Set<Person> peopleExcluded) { // boolean ??
      if(event.isRosterFullyPopulated()){
         return 0; // nothing to fill
      }

      EventRoster currentEventRoster = new EventRoster(event, event.getRoster()); // need to populate (as much as we can...) from event.getRoster()

      int slotsFilled = 0;
      for(int i = 0; i < currentEventRoster.getDutiesAndPeople().size(); i++){
         final Person currentPerson = currentEventRoster.getDutiesAndPeople().get(i).getValue();

         if(currentPerson == null){ // empty slot
            final Duty currentDuty = currentEventRoster.getDutiesAndPeople().get(i).getKey();
            final Person personForDuty = personService.getPersonForDuty(currentDuty, currentEventRoster, peopleExcluded);

            if(personForDuty != null){ // if we found a candidate, add them to Event Roster
               EventRosterItem eri = new EventRosterItem();
               eri.setDuty(currentDuty);
               eri.setPerson(personForDuty);
               eri.setEvent(event);

               event.addEventRosterItem(eri);
               slotsFilled++;
            }
         }
      }

      if(slotsFilled > 0){
         event.setApproved(false); // un-approve !!
         eventRepos.save(event);
      }

      return slotsFilled;
   }

   public EventType saveEventType(final EventType eventType){

      if(EventTypeInterval.DAILY.equals(eventType.getInterval())){
         eventType.setIntervalDetail(null); // clear out interval detail if this is now a daily event type
      }

      /**
       * couldn't figure out how to cleanly handle checkboxes for duties, so manually marshalling Duty objects
       */
      final List<Duty> duties = eventType.getDuties();
      final List<Duty> fixedDuties = new ArrayList<Duty>();
      if(duties != null){
         for(int i = 0, len = duties.size(); i < len; i++){
            final long dutyId = duties.get(i).getId();
            if(dutyId > 0){
               Duty duty = dutyRepos.findOne(dutyId);

               if(duty == null){
                  throw new InvalidIdException("Invalid duty id " + dutyId);
               }

               fixedDuties.add(duty);
            }
         }

         eventType.getDuties().clear();
         eventType.setDuties(fixedDuties);
      }

      return eventTypeRepos.save(eventType);
   }

   public boolean optPersonAndDutyOutOfEvent(@NonNull final Person person, @NonNull final Duty duty, @NonNull final Event event){
      for(EventRosterItem eri : event.getRoster()){
         if(eri.getDuty().getId() == duty.getId() && eri.getPerson().getId() == person.getId()){
            event.getRoster().remove(eri);
            eventRepos.save(event);
            
            // try to repopulate hole in roster 
            // SHOULD THIS HAPPEN AUTOMATICALLY OR BE A MANUAL STEP? Maybe this should be a setting somewhere ...
            boolean slotFilled = this.fillEmptySlots(event, person) == 1;
            logger.debug("optPersonAndDutyOutOfEvent() - was slot replaced successfully ? {}", slotFilled);
            
            return true;
         }
      }

      return false;
   }

   public List<EventCalendarNode> getAllFutureEventCalendarNodes(final Date startDate){
      final List<EventCalendarNode> nodes = new ArrayList<EventCalendarNode>();

      final List<Event> events = eventRepos.findAllByDateEventGreaterThanEqual(startDate);
      for(Event e : events){
         nodes.add(new EventCalendarNode(e.getId(), e.getEventType().getName(), e.getDateEvent(), e.getEventType()));
      }

      Collections.sort(nodes, new EventCalendarNodeSortByDate());
      return nodes;
   }

   public List<EventCalendarNode> getEventCalendarNodesForMonth(final Date monthDate){
      final List<EventCalendarNode> nodes = new ArrayList<EventCalendarNode>();

      final Date startDate = intervalService.getFirstDayOfMonth(monthDate);
      final Date endDate = intervalService.getLastDayOfMonth(startDate);

      final List<Event> events = eventRepos.findEventsByDateBetween(startDate, endDate);
      for(Event e : events){
         nodes.add(new EventCalendarNode(e.getId(), e.getEventType().getName(), e.getDateEvent(), e.getEventType()));
      }

      Collections.sort(nodes, new EventCalendarNodeSortByDate());
      return nodes;
   }

   public int approveAllRosters(){
      return eventRepos.setApprovedStatusOnAllEvents(true);
   }

   public int unApproveAllRosters(){
      return eventRepos.setApprovedStatusOnAllEvents(false);
   }

   public int createAndSaveMissingEvents(){
      final Date maxEventDate = eventRepos.findMaxEventDate();

      if(maxEventDate == null){
         return 0; // there's never even been a normal event generation process, so don't bother trying to create missing events
      }

      final List<Event> missingEvents = getMissingEventsForRange(intervalService.getCurrentSystemDate(), maxEventDate);

      for(Event e : missingEvents){
         EventRoster er = getRosterForEvent(e);
         e.setEventRoster(er);
         updatePreferenceRankingsBasedOnRoster(er);
      }

      eventRepos.save(missingEvents);

      return missingEvents.size();
   }

   /**
    * Get missing events for date range.
    * @param startDate (inclusive)
    * @param endDate (inclusive)
    * @return
    */
   protected List<Event> getMissingEventsForRange(final Date startDate, final Date endDate){
      final List<Event> missingEvents = new ArrayList<Event>();
      final List<EventType> eventTypesWithNoEvents = eventTypeRepos.getEventTypesWithNoEvents();

      for(final EventType et : eventTypesWithNoEvents){
         Date currDate = intervalService.getFirstDayOfMonth(startDate);

         while(currDate.compareTo(endDate) <= 0){
            final List<Date> eventDays = intervalService.getDaysOfMonthForEventType(currDate, et);
            for(Date day : eventDays){
               if(day.compareTo(startDate) >= 0 /*&& day.compareTo(endDate) <= 0*/){ // commented out because we want to generate possible events through EOM
                  Event event = new Event();
                  event.setEventType(et);
                  event.setDateEvent(day);
                  event.setApproved(false);
                  event.setName(et.getName());

                  missingEvents.add(event);
               }
            }

            currDate = intervalService.getFirstDayOfNextMonth(currDate);
         }
      }

      return missingEvents;
   }

   public int createAndSaveEventsForNextMonth(){
      final Date mostRecentGenerationDate = eventRepos.findMaxEventDate();
      Date startDate = null;
      if(mostRecentGenerationDate == null){
         startDate = intervalService.getFirstDayOfMonth(intervalService.getCurrentSystemDate());
      } else {
         startDate = intervalService.getFirstDayOfNextMonth(mostRecentGenerationDate);
      }

      return createAndSaveEventsForMonth(startDate);
   }

   public int createAndSaveEventsForMonth(final Date startDate){

      final Map<EventTypeDetailNode, List<Date>> eventTypeDays = new HashMap<EventTypeDetailNode, List<Date>>();

      for(EventTypeInterval eti : EventTypeInterval.values()){
         List<EventType> eventTypes = eventTypeRepos.findByInterval(eti);
         for(EventType et : eventTypes){

            final EventTypeDetailNode etdn = intervalService.createEventTypeDetailNode(eti, et.getIntervalDetail());
            if(!eventTypeDays.containsKey(etdn)){
               List<Date> results = intervalService.getDaysOfMonthForInterval(startDate, eti, et.getIntervalDetail());
               eventTypeDays.put(etdn, results);
            }
         }
      }

      final List<EventType> allEventTypes = eventTypeRepos.findAll();
      final List<Event> eventsToAdd = new ArrayList<Event>();

      for(EventType et : allEventTypes){
         final EventTypeDetailNode etdn = intervalService.createEventTypeDetailNode(et.getInterval(), et.getIntervalDetail());
         if(eventTypeDays.containsKey(etdn)){
            List<Date> daysToProcessForEvent = eventTypeDays.get(etdn);
            for(Date d : daysToProcessForEvent){
               Event e = new Event();
               e.setDateEvent(d);
               e.setEventType(et);
               e.setName(et.getName());
               e.setApproved(false);
               eventsToAdd.add(e);
            }
         }
      }

      for(Event e : eventsToAdd){
         EventRoster er = getRosterForEvent(e);
         e.setEventRoster(er);
         updatePreferenceRankingsBasedOnRoster(er);
      }

      eventRepos.save(eventsToAdd);

      return eventsToAdd.size();
   }

   public void updatePreferenceRankingsBasedOnRoster(final EventRoster eventRoster){
      // create set of people with duties today
      final Set<Person> peopleWithDuties = new HashSet<Person>();
      for(int i = 0; i < eventRoster.getDutiesAndPeople().size(); i++){
         CollectionUtils.addIgnoreNull(peopleWithDuties, eventRoster.getDutiesAndPeople().get(i).getValue());
      }


      // make them less likely to have to do anything next time (reduce their ranking)
      for(Person p : peopleWithDuties){

         Set<PersonDuty> personDuties = p.getDuties();
         for(PersonDuty pd : personDuties){
            // did this person do this duty today?
            if(personDidThisDuty(p, pd.getDuty(), eventRoster)){
               // make them very unlikely to have to do the same thing again
               pd.setAdjustedPreference(0); 
            } else {
               // make them roughly half as likely to have to do anything going forward
               pd.setAdjustedPreference(pd.getAdjustedPreference() / 2); 
            }
         }
      }

      personRepos.save(peopleWithDuties);

      // increment everyone else's adjusted preference by 1 
      List<Person> peopleNotServing = null;
      if(CollectionUtils.isEmpty(peopleWithDuties)){
         peopleNotServing = personRepos.findAll();
      } else {
         //peopleNotServing = personRepos.findByActiveTrueAndIdNotIn(getIds(peopleWithDuties));
         peopleNotServing = personRepos.findAll(where(isActive()).and(sameOrg()).and(PersonSpecs.idNotIn(getIds(peopleWithDuties)))); //TO DO -- TEST THIS!!!
      }

      if(!CollectionUtils.isEmpty(peopleNotServing)){
         for(Person p : peopleNotServing){
            Set<PersonDuty> personDuties = p.getDuties();
            for(PersonDuty pd : personDuties){
               pd.incrementWeightedPreferenceIfNeeded();
            }
         }
      }

      personRepos.save(peopleNotServing);
   }

   public EventRoster getRosterForEvent(final Event event){

      // populate event roster
      final EventRoster eventRoster = new EventRoster(event);
      for(int i = 0; i < eventRoster.getDutiesAndPeople().size(); i++){
         Person personForDuty = personService.getPersonForDuty(eventRoster.getDutiesAndPeople().get(i).getKey(), eventRoster);
         eventRoster.getDutiesAndPeople().get(i).setValue(personForDuty);
      }

      //updatePreferenceRankingsBasedOnRoster(eventRoster); // <- don't forget to call this from controller once user has "approved" event roster

      return eventRoster;
   }

   public List<EventRosterItem> getSortedRosterIncludingEmptySlots(@NonNull final Long eventId){
      return getSortedRosterIncludingEmptySlots(eventRepos.findOne(eventId));
   }

   public List<EventRosterItem> getSortedRosterIncludingEmptySlots(@NonNull final Event event){
      final List<Duty> allDutiesForEventType = event.getEventType().getDuties();
      List<EventRosterItem> sortedRoster = new ArrayList<EventRosterItem>(event.getRoster());

      /**
       * If we just sorted our List<EventRosterItem> and returned it, we would not be
       * including unassigned slots. The following adds "empty" records as needed to
       * ensure blank slots are displayed in application where needed.
       */
      if(sortedRoster.size() < allDutiesForEventType.size()){
         for(Duty d : allDutiesForEventType){
            int expectedDutyCount = getNumberOccurencesDuty(allDutiesForEventType, d);
            int actualDutyCount = getNumberOccurencesDuty(getDuties(sortedRoster), d);

            if(expectedDutyCount != actualDutyCount){
               for(int i = actualDutyCount; i < expectedDutyCount; i++){
                  EventRosterItem eri = new EventRosterItem();
                  eri.setDuty(d);
                  eri.setEvent(event);
                  eri.setPerson(null);
                  sortedRoster.add(eri);
               }
            }
         }
      }

      Collections.sort(sortedRoster, new EventRosterItemSortByDutyOrder());

      return sortedRoster;
   }

   private List<Duty> getDuties(List<EventRosterItem> items){
      List<Duty> duties = new ArrayList<Duty>();

      if(items != null){
         for(EventRosterItem eri : items){
            duties.add(eri.getDuty());
         }
      }

      return duties;
   }

   private int getNumberOccurencesDuty(List<Duty> duties, Duty duty){
      int num = 0;

      if(duties != null){
         for(Duty d : duties){
            if(d.getId() == duty.getId()){
               num++;
            }
         }
      }

      return num;
   }

   private static boolean personDidThisDuty(final Person person, final Duty duty, final EventRoster eventRoster){

      for(int i = 0; i < eventRoster.getDutiesAndPeople().size(); i++){
         final Duty d = eventRoster.getDutiesAndPeople().get(i).getKey();
         if(d != null && d.getId() == duty.getId()){
            final Person dutyDoer = eventRoster.getDutiesAndPeople().get(i).getValue();
            return dutyDoer != null && dutyDoer.getId() == person.getId();
         }
      }

      return false;
   }

   private static Set<Long> getIds(@NonNull Set<Person> people){
      Set<Long> ids = new HashSet<Long>(people.size());

      for(Person p : people){
         ids.add(p.getId());
      }

      return ids;

   }
}
