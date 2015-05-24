package com.dak.duty.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.model.Event;
import com.dak.duty.repository.EventRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
   
   private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
   
   @Autowired
   EventRepository eventRepos;
   
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String getAdminHome(Model model){
      logger.debug("getAdminHome()");
      
      final Page<Event> events = eventRepos.findAllByOrderByDateEventDescIdDesc(new PageRequest(0, 20));
      logger.info("events found: {}", events.getContent().size());
      
      model.addAttribute("events", events.getContent());
      return "admin/admin";
   }
}
