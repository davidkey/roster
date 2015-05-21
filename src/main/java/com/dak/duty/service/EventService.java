package com.dak.duty.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;

@Service
@Transactional
public class EventService {

   @Autowired
   PersonRepository personRepos;
   
   @Autowired 
   PersonService personService;
   
   public EventRoster getRosterForEvent(final Event event){
      final EventRoster er = new EventRoster(event);
      
      for(Duty d : event.getEventType().getDuties()){
         Person personForDuty = personService.getPersonForDuty(d, er);
         er.getDutiesAndPeople().put(d, personForDuty);
      }
      
      return er;
   }
}
