package com.dak.duty.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.EventService;
import com.dak.duty.service.IntervalService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/test")
public class TestController {
   
   private static final Logger logger = LoggerFactory.getLogger(TestController.class);

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
   
   @Autowired
   IntervalService intervalService;
   
   @RequestMapping("/event/{eventId}")
   final @ResponseBody Event getEvent(@PathVariable final Long eventId){
      return eventRepos.findOne(eventId);
   }
   
   @RequestMapping("/intervalService/{eventTypeInterval}/{detail}")
   final @ResponseBody List<Date> tryIntervalService(@PathVariable("eventTypeInterval") String etis, @PathVariable("detail") String detail){
      EventTypeInterval eti = EventTypeInterval.valueOf(etis);
      
      List<Date> dates = intervalService.getDaysOfQuarterForInterval(new Date(), eti, detail);
      logger.info("dates: {}", dates);
      
      return dates;
      
   }
   
   @RequestMapping("/eventRepository/{eventTypeId}")
   final @ResponseBody List<Event> tryEventRepos(@PathVariable Long eventTypeId){
      final EventType et = eventTypeRepos.findOne(eventTypeId);
      return eventRepos.findMostRecentEventsByEventType(et);
   }

   @RequestMapping("/eventService")
   final @ResponseBody Event eventService(){
      Event event = eventRepos.findOne(1L);
      EventRoster er = eventService.getRosterForEvent(event);

      event.setEventRoster(er);
      eventRepos.saveAndFlush(event); // have to flush after clearing event roster items (setEventRoster())

      return event;
   }

   @RequestMapping("/personService/{dutyId}")
   final @ResponseBody Person tryPersonService(@PathVariable Long dutyId){
      final Duty d = dutyRepos.findOne(dutyId);
      return personService.getPersonForDuty(d, null);
   }

   public static final class EventNode {
      private final Event event;
      private final Object[] duties;

      public EventNode(final Event event, final Object[] nodes){
         this.event = event;
         
         if(event != null){
            event.getEventType().setDuties(null); // don't show duties in this json result -- too wordy
         }
         
         this.duties = nodes;
      }

      public Event getEvent() {
         return event;
      }

      public Object[] getDuties() {
         return duties;
      }
   }

   public static final class DutyPersonNode{
      private final Duty duty;
      private final Person person;

      public DutyPersonNode (final Duty duty, final Person person){
         this.duty = duty;

         if(person != null){ // don't show duties in this json result -- too wordy
            person.setDuties(null);
         }

         this.person = person;
      }

      public Duty getDuty() {
         return duty;
      }

      public Person getPerson() {
         return person;
      }
   }

}
