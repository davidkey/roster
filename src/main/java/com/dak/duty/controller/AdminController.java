package com.dak.duty.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
   
   private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
   
   @Autowired
   EventRepository eventRepos;
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   EventTypeRepository eventTypeRepos;
   
   @Autowired
   DutyRepository dutyRepos;
   
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String getAdminHome(Model model){
      logger.debug("getAdminHome()");
      
      final Page<Event> events = eventRepos.findAllByOrderByDateEventDescIdDesc(new PageRequest(0, 20));
      logger.info("events found: {}", events.getContent().size());
      
      model.addAttribute("events", events.getContent());
      return "admin/admin";
   }
   
   @RequestMapping(value = "/people", method = RequestMethod.GET)
   public String getPeople(Model model){
      logger.debug("getPeople()");
      
      final List<Person> people = personRepos.findAllByOrderByNameLastAscNameFirstAsc();
      logger.debug("people found: {}", people.size());
      
      model.addAttribute("people", people);
      return "admin/people";
   }
   
   @RequestMapping(value = "/rosters", method = RequestMethod.GET)
   public String getRostersAndEvents(Model model){
      logger.debug("getRostersAndEvents()");
      
      final List<Event> events = eventRepos.findAll();
      logger.info("events found: {}", events.size());
      
      model.addAttribute("events", events);
      return "admin/rosters";
   }
   
   @RequestMapping(value = "/eventScheduling", method = RequestMethod.GET)
   public String getEventTypes(Model model){
      logger.debug("getEventTypes()");
      
      final List<EventType> eventTypes = eventTypeRepos.findAll();
      logger.info("event types found: {}", eventTypes.size());
      
      model.addAttribute("eventTypes", eventTypes);
      return "admin/eventScheduling";
   }
   
   @RequestMapping(value = "/dutyManagement", method = RequestMethod.GET)
   public String getDuties(Model model){
      logger.debug("getDuties()");
      
      final List<Duty> duties = dutyRepos.findAll();
      logger.info("duties found: {}", duties.size());
      
      model.addAttribute("duties", duties);
      return "admin/dutyManagement";
   }
   
   @RequestMapping(value = "/settings", method = RequestMethod.GET)
   public String getSettings(Model model){
      logger.debug("getSettings()");
      return "admin/settings";
   }
   
   @RequestMapping(value = "/about", method = RequestMethod.GET)
   public String getAbout(Model model){
      logger.debug("getAbout()");
      return "admin/about";
   }
}
