package com.dak.duty.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.EventService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/admin/eventTypes")
public class EventTypeAdminController {

   private static final Logger logger = LoggerFactory.getLogger(EventTypeAdminController.class);
   
   @Autowired
   EventTypeRepository eventTypeRepos;
   
   @Autowired
   EventService eventService;
   
   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   PersonService personService;
   
   @Autowired
   IAuthenticationFacade authenticationFacade;

   
   @RequestMapping(method = RequestMethod.GET)
   public String getEventTypes(Model model){
      logger.debug("getEventTypes()");

      final List<EventType> eventTypes = eventTypeRepos.findByActiveTrue();
      logger.debug("event types found: {}", eventTypes.size());

      model.addAttribute("eventTypes", eventTypes);
      return "admin/eventTypes";
   }

   @RequestMapping(method = RequestMethod.POST)
   public String saveEventType(@ModelAttribute @Valid EventType eventType, BindingResult result, final RedirectAttributes redirectAttributes){
      logger.debug("saveEventType()");
      final boolean alreadyExisted = eventType.getId() > 0;

      if(result.hasErrors()){
         redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.eventType", result);
         redirectAttributes.addFlashAttribute("eventType", eventType);
         
         if(alreadyExisted){
            return "redirect:/admin/eventTypes/" + eventType.getId();
         } else {
            return "redirect:/admin/eventTypes/new";
         }
      }
      
      if(alreadyExisted && !eventTypeRepos.findOne(eventType.getId()).getOrganisation().equals(authenticationFacade.getOrganisation())){
         throw new SecurityException("can't do that");
      } else { 
         eventType.setOrganisation(authenticationFacade.getOrganisation());
      }
      
      eventService.saveEventType(eventType);
      redirectAttributes.addFlashAttribute("msg_success", alreadyExisted ? "Event Type updated!" : "Event Type added!");
      return "redirect:/admin/eventTypes";
   }

   @PreAuthorize("#e.organisation.id == principal.person.organisation.id")
   @RequestMapping(value = "/{eventTypeId}", method = RequestMethod.GET)
   public String getEventTypeById(@PathVariable("eventTypeId") @P("e") EventType eventType, Model model){
      logger.debug("getEventTypeById({})", eventType.getId());

      if(!model.containsAttribute("eventType")){
         model.addAttribute("eventType", eventType);
      }
      model.addAttribute("eventTypeIntervals", EventTypeInterval.values());
      model.addAttribute("allPossibleDuties", dutyRepos.findByActiveTrue());
      return "admin/eventType";
   }

   @RequestMapping(value = "/new", method = RequestMethod.GET)
   public String getAddEventType(Model model){
      logger.debug("getAddEventType()");

      if(!model.containsAttribute("eventType")){
         model.addAttribute("eventType", new EventType());
      }
      
      model.addAttribute("eventTypeIntervals", EventTypeInterval.values());
      model.addAttribute("allPossibleDuties", dutyRepos.findByActiveTrue());
      return "admin/eventType";
   }
}
