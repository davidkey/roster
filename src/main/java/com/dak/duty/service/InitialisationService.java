package com.dak.duty.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

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

@Service
@Transactional
public class InitialisationService {

   private static final Logger logger = LoggerFactory.getLogger(InitialisationService.class);
   private static final DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
   
   @Autowired
   EventTypeRepository eventTypeRepos;
   
   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   EventRepository eventRepos;
   
   protected void clearAllData(){
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
      clearAllData();
      
      final List<Duty> defaultDuties = getDefaultDuties();
      dutyRepos.save(defaultDuties);
      
      final List<EventType> defaultEventTypes = getDefaultEventTypes(defaultDuties);
      eventTypeRepos.save(defaultEventTypes);
      
      final List<Person> defaultPeople = getDefaultPeople(defaultDuties); 
      personRepos.save(defaultPeople);
      
      final List<Event> defaultEvents = getDefaultEvents(defaultEventTypes);
      eventRepos.save(defaultEvents);
      
   }
   
   protected List<Event> getDefaultEvents(final List<EventType> eventTypes){
      final List<Event> events = new ArrayList<Event>(eventTypes.size());
      
      for(EventType et : eventTypes){
         final Event e = new Event();
        
         e.setName(et.getName());
         try{
            if(e.getName().contains("Sunday")){
               e.setDateEvent(format.parse("05/24/2015")); // sunday
            } else {
               e.setDateEvent(format.parse("05/27/2015")); // wednesday
            }
         } catch (ParseException pe){ }
         e.setEventType(et);
        
         events.add(e);
      }
      
      return events;
   }
   
   @SuppressWarnings("deprecation")
   protected List<Person> getDefaultPeople(final List<Duty> duties){
      final List<Person> people = new ArrayList<Person>();
      DataFactory df = new DataFactory();
      RandomDataGenerator randomData = new RandomDataGenerator();
      
      final List<Duty> scrambledDuties = new ArrayList<Duty>(duties);
      for(int i = 0; i < 100; i++){
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
      duty.setName("Annoucements");
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
            case "Annoucements":
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
      
      final EventType sundayPm = new EventType();
      sundayPm.setName("Sunday PM");
      sundayPm.setDescription(sundayPm.getName());
      sundayPm.setDuties(sundayPmDuties);
      sundayPm.setInterval(EventTypeInterval.WEEKLY);
      sundayPm.setIntervalDetail(IntervalWeekly.SUNDAY.toString());
      
      final EventType wednesday = new EventType();
      wednesday.setName("Wednesday PM");
      wednesday.setDescription(wednesday.getName());
      wednesday.setDuties(wednesdayDuties);
      wednesday.setInterval(EventTypeInterval.WEEKLY);
      wednesday.setIntervalDetail(IntervalWeekly.WEDNESDAY.toString());
      
      final List<EventType> eventTypes = new ArrayList<EventType>();
      eventTypes.add(sundayAm);
      eventTypes.add(sundayPm);
      eventTypes.add(wednesday);
      
      return eventTypes;
   }
}
