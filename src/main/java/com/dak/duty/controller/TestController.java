package com.dak.duty.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.EventService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/test")
public class TestController {

   @Autowired
   DutyRepository dutyRepos;

   @Autowired
   EventTypeRepository eventTypeRepos;
   
   @Autowired
   EventRepository eventRepos;
   
   @Autowired
   PersonService personService;
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   EventService eventService;
   
   @RequestMapping("/eventService")
   final @ResponseBody EventRoster tryEventServiceByEventType(){
      Event event = eventRepos.findOne(1L);
      return eventService.getRosterForEvent(event);
   }
   
   
   @RequestMapping("/personService/{dutyId}")
   final @ResponseBody Person tryPersonService(@PathVariable Long dutyId){
      final Duty d = dutyRepos.findOne(dutyId);
      return personService.getPersonForDuty(d, null);
   }
   
   @RequestMapping("/addSomePeople")
   final @ResponseBody List<Person> addSomePeople(){
      Person p = new Person();
      p.setActive(true);
      p.setNameFirst("Byron");
      p.setNameLast("Nash");
      p.setEmailAddress("bnash@google.com");
      p.addDutyAndPreference(dutyRepos.findOne(2L), 9);
      
      personRepos.save(p);
      
      p = new Person();
      p.setActive(true);
      p.setNameFirst("John");
      p.setNameLast("Galt");
      p.setEmailAddress("jgalt@google.com");
      p.addDutyAndPreference(dutyRepos.findOne(2L), 1); // hates this stuff
      
      personRepos.save(p);
      
      
      return personRepos.findAll();
   }
   
   @RequestMapping("/event")
   public @ResponseBody String addEvent() throws ParseException{
      final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/YYYY");
      
      EventType et = eventTypeRepos.findByName("Sunday AM");
      if(et == null){
         return "uhoh - event type not found";
      }
      
      Event e = new Event();
      e.setEventType(et);
      e.setDateEvent(formatter.parse("05/24/2015"));
      e.setName(et.getName());
      
      eventRepos.save(e);

      return "done";
   }
   

   @RequestMapping("/eventType")
   public @ResponseBody String addEventTypeWithDuties(){
      List<Duty> duties = dutyRepos.findAll();


      EventType et = new EventType();
      et.setName("Sunday AM");
      et.setDescription(et.getName());

      for(Duty d : duties){
         et.addDuty(d);
      }
      
      eventTypeRepos.save(et);

      return "done";
   }
   
   @RequestMapping("/duties")
   public @ResponseBody String addSomeDuties(){
      List<Duty> duties = dutyRepos.findAll();

      if(duties != null && duties.size() > 2){
         return "already done";
      }

      Duty d = new Duty();
      d.setName("Scripture Reading");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      d = new Duty();
      d.setName("Opening Prayer");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      d = new Duty();
      d.setName("Closing Prayer");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
     /* d = new Duty();
      d.setName("Invitation");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      */
      
      d = new Duty();
      d.setName("Annoucements");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      d = new Duty();
      d.setName("Table 1");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      d = new Duty();
      d.setName("Table 2");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      d = new Duty();
      d.setName("Table 3");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      d = new Duty();
      d.setName("Table 4");
      d.setDescription(d.getName());
      dutyRepos.saveAndFlush(d);
      
      
      return "done";
      
   }
   

}
