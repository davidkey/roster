package com.dak.duty.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import lombok.NonNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
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
   
   public void createMissingEventsForCurrentMonth(){
      /**
       * should be easy enough to implement. 
       * 
       * just look for any events and their associated dates (by interval, detail)
       * that don't have an Event record in db.
       */
      throw new NotImplementedException("need to add this method");
   }

   public void createAndSaveEventsForNextMonth(){
      final Date mostRecentGenerationDate = eventRepos.findMaxEventDate();
      Date startDate = null;
      if(mostRecentGenerationDate == null){
         startDate = intervalService.getFirstDayOfMonth(new Date());
      } else {
         startDate = intervalService.getFirstDayOfNextMonth(mostRecentGenerationDate);
      }
      
      createAndSaveEventsForMonth(startDate);
   }
   
   public void createAndSaveEventsForMonth(final Date startDate){

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
