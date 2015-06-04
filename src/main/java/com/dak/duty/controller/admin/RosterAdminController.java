package com.dak.duty.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.model.Event;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.service.EventService;

@Controller
@RequestMapping("/admin/rosters")
public class RosterAdminController {
   
   private static final Logger logger = LoggerFactory.getLogger(RosterAdminController.class);
   
   @Autowired
   EventRepository eventRepos;
   
   @Autowired
   EventService eventService;

   @RequestMapping(method = RequestMethod.GET)
   public String getRostersAndEvents(Model model){
      logger.debug("getRostersAndEvents()");

      final List<Event> events = eventRepos.findAll();
      logger.debug("events found: {}", events.size());

      model.addAttribute("events", events);
      return "admin/rosters";
   }

   @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
   public String getEventAndRoster(@PathVariable Long eventId, Model model){
      logger.debug("getEventAndRoster({})", eventId);

      final Event event = eventRepos.findOne(eventId);
      final List<EventRosterItem> sortedRoster = eventService.getSortedRosterIncludingEmptySlots(event);

      model.addAttribute("event", event);
      model.addAttribute("roster", sortedRoster);
      return "admin/roster";
   }

   @RequestMapping(value = "/generate", method = RequestMethod.GET)
   public String generateRosters(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("generateRosters()");

      final int numGenerated = eventService.createAndSaveEventsForNextMonth();

      redirectAttributes.addFlashAttribute("msg_success", numGenerated + " rosters generated!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/generateMissing", method = RequestMethod.GET)
   public String generateMissingRosters(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("generateMissingRosters()");

      final int numGenerated = eventService.createAndSaveMissingEvents();

      redirectAttributes.addFlashAttribute("msg_success", numGenerated + " missing rosters generated!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/approveAllFullyPopulated", method = RequestMethod.GET)
   public String approveAllFullyPopulated(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("approveAllFullyPopulated()");

      final List<Event> events = eventRepos.findByApproved(false);
      final List<Event> eventsToApprove = new ArrayList<Event>();

      for(Event e : events){
         if(e.isRosterFullyPopulated()){
            e.setApproved(true);
            eventsToApprove.add(e);
         }
      }

      eventRepos.save(eventsToApprove);

      redirectAttributes.addFlashAttribute("msg_success", eventsToApprove.size() + " rosters approved!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/approveAll", method = RequestMethod.GET)
   public String approveAll(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("approveAll()");

      final int numAffected = eventService.approveAllRosters();

      redirectAttributes.addFlashAttribute("msg_success", numAffected + " rosters approved!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/unapproveAll", method = RequestMethod.GET)
   public String unapproveAll(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("unapproveAll()");

      final int numAffected = eventService.unApproveAllRosters();

      redirectAttributes.addFlashAttribute("msg_success", numAffected + " rosters unapproved!");
      return "redirect:/admin/rosters";
   }
}
