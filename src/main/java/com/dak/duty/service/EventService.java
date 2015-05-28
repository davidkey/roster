package com.dak.duty.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.model.comparable.EventRosterItemSortByDutyOrder;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.IntervalService.EventTypeDetailNode;

@Service
@Transactional
public class EventService {

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

      return missingEvents == null ? 0 : missingEvents.size();
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
         startDate = intervalService.getFirstDayOfMonth(new Date());
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

      return eventsToAdd == null ? 0 : eventsToAdd.size();
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
               pd.setAdjustedPreference((int)Math.floor(pd.getAdjustedPreference() / 2)); 
            }
         }
      }

      personRepos.save(peopleWithDuties);

      // increment everyone else's adjusted preference by 1 
      List<Person> peopleNotServing = null;
      if(CollectionUtils.isEmpty(peopleWithDuties)){
         peopleNotServing = personRepos.findAll();
      } else {
         peopleNotServing = personRepos.findByActiveTrueAndIdNotIn(getIds(peopleWithDuties));
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
            int actualDutyCount = getNumberOccurencesDutyForEventRosterItem(sortedRoster, d);

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
   
   private int getNumberOccurencesDuty(List<Duty> duties, Duty duty){
      int num = 0;

      for(Duty d : duties){
         if(d.getId() == duty.getId()){
            num++;
         }
      }

      return num;
   }

   private int getNumberOccurencesDutyForEventRosterItem(List<EventRosterItem> items, Duty duty){
      int num = 0;

      for(EventRosterItem eri : items){
         if(eri.getDuty().getId() == duty.getId()){
            num++;
         }
      }

      return num;
   }

   //FIXME: kludge until I can figure out how I borked equals / hashcode -- should be able to [a.equals(b)] but have to use [a.getId() == b.getId()]
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
