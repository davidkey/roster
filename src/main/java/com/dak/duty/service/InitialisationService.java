package com.dak.duty.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;

import lombok.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Service
@Transactional
public class InitialisationService {

   private static final Logger logger = LoggerFactory.getLogger(InitialisationService.class);
   public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
   
   @Autowired
   EventTypeRepository eventTypeRepos;
   
   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   EventRepository eventRepos;
   
   @Autowired
   IntervalService intervalService;
   
   protected void clearAllData(){
      logger.info("clearAllData");
      
      eventRepos.deleteAll();
      eventRepos.flush();
      
      personRepos.deleteAll();
      personRepos.flush();
      
      eventTypeRepos.deleteAll();
      eventTypeRepos.flush();
      
      dutyRepos.deleteAll();
      dutyRepos.flush();
   }
   
   public void populateDefaultData(){
      logger.info("populateDefaultData");
      clearAllData();
      
      final List<Duty> defaultDuties = getDefaultDuties();
      logger.debug("defaultDuties: {}", defaultDuties);
      dutyRepos.save(defaultDuties);
      
      final List<EventType> defaultEventTypes = getDefaultEventTypes(defaultDuties);
      logger.debug("defaultEventTypes: {}", defaultEventTypes);
      eventTypeRepos.save(defaultEventTypes);
      
      final List<Person> defaultPeople = getDefaultPeople(defaultDuties); 
      logger.debug("defaultPeople: {}", defaultPeople);
      personRepos.save(defaultPeople);
      
      final List<Event> defaultEvents = getDefaultEvents(defaultEventTypes);
      logger.debug("defaultEvents: {}", defaultEvents);
      eventRepos.save(defaultEvents);
      
   }
   
   protected List<Event> getDefaultEvents(final List<EventType> eventTypes){
      final List<Event> events = new ArrayList<Event>(eventTypes.size());
      
      for(EventType et : eventTypes){
         final Event e = new Event();
        
         e.setName(et.getName());
         try{
            if(e.getName().contains("Sunday")){
               e.setDateEvent(fmt.parseDateTime("05/24/2015").toDate()); // sunday
            } else {
               e.setDateEvent(fmt.parseDateTime("05/27/2015").toDate()); // wednesday
            }
         } catch (IllegalArgumentException iae){
            // do nothing
         }
         
         e.setEventType(et);
        
         events.add(e);
      }
      
      return events;
   }
   
   protected List<Person> getDefaultPeople(final List<Duty> duties){
      final List<Person> people = new ArrayList<Person>();
      DataFactory df = new DataFactory();
      RandomDataGenerator randomData = new RandomDataGenerator();
      
      final List<Duty> scrambledDuties = new ArrayList<Duty>(duties);
      for(int i = 0; i < 25; i++){
         Person p = new Person();
         p.setActive(true);
         p.setEmailAddress(df.getEmailAddress());
         p.setNameFirst(df.getFirstName());
         p.setNameLast(df.getLastName());
         
         Collections.shuffle(scrambledDuties);
         final int numDuties = randomData.nextInt(2, scrambledDuties.size()-1);
         for(int x = 0; x < numDuties; x++){
            p.addDutyAndPreference(scrambledDuties.get(x), randomData.nextInt(1, 9));
         }
         
         people.add(p);
      }
      
      return people;
   }
   
   protected List<Duty> getDefaultDuties(){
      final List<Duty> duties = new ArrayList<Duty>();
      
      Duty duty = null;
      int count = 1;
      
      duty = new Duty();
      duty.setName("Song Leading");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Opening Prayer");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Closing Prayer");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Announcements");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Scripture Reading");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Preaching");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Table");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);

      duty = new Duty();
      duty.setName("Invitation");
      duty.setDescription(duty.getName());
      duty.setSortOrder(count++);
      duties.add(duty);
      
      return duties;
   }
   
   protected List<EventType> getDefaultEventTypes(@NonNull final List<Duty> duties){
      final List<Duty> sundayAmDuties = new ArrayList<Duty>();
      final List<Duty> sundayPmDuties  = new ArrayList<Duty>();
      final List<Duty> wednesdayDuties = new ArrayList<Duty>();
      
      for(Duty d : duties){
         switch(d.getName()){
            case "Song Leading":
            case "Opening Prayer":
            case "Closing Prayer":
            case "Announcements":
               sundayAmDuties.add(d);
               sundayPmDuties.add(d);
               wednesdayDuties.add(d);
               break;
            case "Scripture Reading":
            case "Preaching":
               sundayAmDuties.add(d);
               sundayPmDuties.add(d);
               break;
            case "Table":
               sundayAmDuties.add(d);
               sundayAmDuties.add(d);
               sundayAmDuties.add(d);
               sundayAmDuties.add(d);
               sundayPmDuties.add(d);
               break;
            case "Invitation":
               wednesdayDuties.add(d);
               break;               
            default:
               logger.warn("possible problem - we've got a default duty ({}) without an associated event.", d);
               break;
         }
      }
      
      final EventType sundayAm = new EventType();
      sundayAm.setName("Sunday AM");
      sundayAm.setDescription(sundayAm.getName());
      sundayAm.setDuties(sundayAmDuties);
      sundayAm.setInterval(EventTypeInterval.WEEKLY);
      sundayAm.setIntervalDetail(IntervalWeekly.SUNDAY.toString());
      sundayAm.setStartTime(intervalService.getTimeWithoutDate(9, 30));
      sundayAm.setEndTime(intervalService.getTimeWithoutDate(11, 30));
      
      final EventType sundayPm = new EventType();
      sundayPm.setName("Sunday PM");
      sundayPm.setDescription(sundayPm.getName());
      sundayPm.setDuties(sundayPmDuties);
      sundayPm.setInterval(EventTypeInterval.WEEKLY);
      sundayPm.setIntervalDetail(IntervalWeekly.SUNDAY.toString());
      sundayPm.setStartTime(intervalService.getTimeWithoutDate(18, 30));
      sundayPm.setEndTime(intervalService.getTimeWithoutDate(20, 0));
      
      final EventType wednesday = new EventType();
      wednesday.setName("Wednesday PM");
      wednesday.setDescription(wednesday.getName());
      wednesday.setDuties(wednesdayDuties);
      wednesday.setInterval(EventTypeInterval.WEEKLY);
      wednesday.setIntervalDetail(IntervalWeekly.WEDNESDAY.toString());
      wednesday.setStartTime(intervalService.getTimeWithoutDate(19, 30));
      wednesday.setEndTime(intervalService.getTimeWithoutDate(21, 0));
      
      final List<EventType> eventTypes = new ArrayList<EventType>();
      eventTypes.add(sundayAm);
      eventTypes.add(sundayPm);
      eventTypes.add(wednesday);
      
      return eventTypes;
   }
}
