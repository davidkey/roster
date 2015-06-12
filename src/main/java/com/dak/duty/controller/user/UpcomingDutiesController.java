package com.dak.duty.controller.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.EventService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/user/upcomingDuties")
public class UpcomingDutiesController {
   
   private static final Logger logger = LoggerFactory.getLogger(UpcomingDutiesController.class);
   
   @Autowired
   PersonService personService;
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   EventRepository eventRepos;
   
   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   EventService eventService;
   
   @RequestMapping(value = "/count", method = RequestMethod.GET)
   public @ResponseBody int getUpcomingDutiesCount(Principal principal){
      logger.debug("getUpcomingDutiesCount()");
      
      Person p = personRepos.findByEmailAddress(principal.getName());
      return personService.getUpcomingDuties(p).size();
   }
   
   @RequestMapping(method = RequestMethod.GET)
   public String getUpcomingDutiesAll(Principal principal, Model model){
      logger.debug("getUpcomingDutiesCount()");
      model.addAttribute("upcomingDuties", personService.getUpcomingDuties(personRepos.findByEmailAddress(principal.getName())));
      return "user/duties";
   }
   
   @RequestMapping(value = "/optOut", method = RequestMethod.POST)
   public @ResponseBody JsonResponse optOut(@ModelAttribute("dutyId") Duty duty, @ModelAttribute("eventId") Event event, Principal principal){
      logger.debug("optOut()");
      
      final Person person = personRepos.findByEmailAddress(principal.getName());
      
      if(eventService.optPersonAndDutyOutOfEvent(person, duty, event)){
         return new JsonResponse(ResponseStatus.OK, "Opted out.");
      } else {
         return new JsonResponse(ResponseStatus.ERROR, "Opting out failed!");
      }
   }
}
