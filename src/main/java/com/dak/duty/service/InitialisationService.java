package com.dak.duty.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import lombok.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.EventTypeRepository;

@Service
@Transactional
public class InitialisationService {

   private static final Logger logger = LoggerFactory.getLogger(InitialisationService.class);
   
   @Autowired
   EventTypeRepository eventTypeRepos;
   
   
   public void populateDefaultData(){
      final List<Duty> defaultDuties = getDefaultDuties();
      final List<EventType> defaultEventTypes = getDefaultEventTypes(defaultDuties);
      
      
      //TODO: persist defaults
   }
   
   public List<Duty> getDefaultDuties(){
      final List<Duty> duties = new ArrayList<Duty>();
      
      Duty duty = null;
      
      duty = new Duty();
      duty.setName("Song Leading");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Opening Prayer");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Closing Prayer");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Annoucements");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Scripture Reading");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Preaching");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Table");
      duty.setDescription(duty.getName());
      duties.add(duty);

      duty = new Duty();
      duty.setName("Invitation");
      duty.setDescription(duty.getName());
      duties.add(duty);
      
      return duties;
   }
   
   public List<EventType> getDefaultEventTypes(@NonNull final List<Duty> duties){
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
               break;
            case "Invitation":
               wednesdayDuties.add(d);
               break;               
            default:
               // possible problem - we've got a default duty without an event
               logger.warn("possible problem - we've got a default duty ({}) without an associated event.", d);
               break;
         }
      }
      
      final EventType sundayAm = new EventType();
      sundayAm.setName("Sunday AM");
      sundayAm.setDescription(sundayAm.getName());
      sundayAm.setDuties(sundayAmDuties);
      sundayAm.setInterval(EventTypeInterval.WEEKLY);
      
      final EventType sundayPm = new EventType();
      sundayPm.setName("Sunday PM");
      sundayPm.setDescription(sundayPm.getName());
      sundayPm.setDuties(sundayPmDuties);
      sundayPm.setInterval(EventTypeInterval.WEEKLY);
      
      final EventType wednesday = new EventType();
      wednesday.setName("Wednesday PM");
      wednesday.setDescription(wednesday.getName());
      wednesday.setDuties(wednesdayDuties);
      wednesday.setInterval(EventTypeInterval.WEEKLY);
      
      final List<EventType> eventTypes = new ArrayList<EventType>();
      eventTypes.add(sundayAm);
      eventTypes.add(sundayPm);
      eventTypes.add(wednesday);
      
      return eventTypes;
   }
}
