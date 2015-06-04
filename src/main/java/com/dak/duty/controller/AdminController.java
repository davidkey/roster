package com.dak.duty.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.DutyService;
import com.dak.duty.service.EventService;
import com.dak.duty.service.IntervalService;
import com.dak.duty.service.container.EventCalendarNode;

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

   @Autowired
   EventService eventService;

   @Autowired
   DutyService dutyService;

   @Autowired
   IntervalService intervalService;

   @RequestMapping(method = RequestMethod.GET)
   public String getAdminHome(Model model){
      logger.debug("getAdminHome()");
      return "admin/admin";
   }

   @RequestMapping(value = "/events/{year}/{month}/json", method = RequestMethod.GET)
   public @ResponseBody List<EventCalendarNode> getEventCalendarItems(@PathVariable("year") final Integer year, @PathVariable("month") final Integer month) throws ParseException{
      logger.info("getEventCalendarItems({}, {})", year, month);
      final Date monthDate = new DateTime(year, month, 1, 0, 0 ,0).toDate();
      return eventService.getEventCalendarNodesForMonth(monthDate);
   }
   
   @RequestMapping(value = "/events/all/json", method = RequestMethod.GET)
   public @ResponseBody List<EventCalendarNode> getFutureEventCalendarItems(){
      return eventService.getAllFutureEventCalendarNodes(intervalService.getFirstDayOfMonth(intervalService.getCurrentSystemDate()));
   }

   @RequestMapping(value = "/events/current/json", method = RequestMethod.GET)
   public @ResponseBody List<EventCalendarNode> getCurrentEventCalendarItems(){
      return eventService.getEventCalendarNodesForMonth(intervalService.getCurrentSystemDate());
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
      logger.debug("events found: {}", events.size());

      model.addAttribute("events", events);
      return "admin/rosters";
   }



   @RequestMapping(value = "/rosters/{eventId}", method = RequestMethod.GET)
   public String getEventAndRoster(@PathVariable Long eventId, Model model){
      logger.debug("getEventAndRoster({})", eventId);

      final Event event = eventRepos.findOne(eventId);
      final List<EventRosterItem> sortedRoster = eventService.getSortedRosterIncludingEmptySlots(event);

      model.addAttribute("event", event);
      model.addAttribute("roster", sortedRoster);
      return "admin/roster";
   }

   @RequestMapping(value = "/rosters/generate", method = RequestMethod.GET)
   public String generateRosters(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("generateRosters()");

      final int numGenerated = eventService.createAndSaveEventsForNextMonth();

      redirectAttributes.addFlashAttribute("msg_success", numGenerated + " rosters generated!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/rosters/generateMissing", method = RequestMethod.GET)
   public String generateMissingRosters(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("generateMissingRosters()");

      final int numGenerated = eventService.createAndSaveMissingEvents();

      redirectAttributes.addFlashAttribute("msg_success", numGenerated + " missing rosters generated!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/rosters/approveAllFullyPopulated", method = RequestMethod.GET)
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

   @RequestMapping(value = "/rosters/approveAll", method = RequestMethod.GET)
   public String approveAll(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("approveAll()");

      final int numAffected = eventService.approveAllRosters();

      redirectAttributes.addFlashAttribute("msg_success", numAffected + " rosters approved!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/rosters/unapproveAll", method = RequestMethod.GET)
   public String unapproveAll(Model model, final RedirectAttributes redirectAttributes){
      logger.debug("unapproveAll()");

      final int numAffected = eventService.unApproveAllRosters();

      redirectAttributes.addFlashAttribute("msg_success", numAffected + " rosters unapproved!");
      return "redirect:/admin/rosters";
   }

   @RequestMapping(value = "/eventTypes", method = RequestMethod.GET)
   public String getEventTypes(Model model){
      logger.debug("getEventTypes()");

      final List<EventType> eventTypes = eventTypeRepos.findByActiveTrue();
      logger.debug("event types found: {}", eventTypes.size());

      model.addAttribute("eventTypes", eventTypes);
      return "admin/eventTypes";
   }

   @RequestMapping(value = "/eventTypes", method = RequestMethod.POST)
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

      eventService.saveEventType(eventType);
      redirectAttributes.addFlashAttribute("msg_success", alreadyExisted ? "Event Type updated!" : "Event Type added!");
      return "redirect:/admin/eventTypes";
   }

   @RequestMapping(value = "/eventTypes/{eventTypeId}", method = RequestMethod.GET)
   public String getEventTypeById(@PathVariable long eventTypeId, Model model){
      logger.debug("getEventTypeById({})", eventTypeId);

      if(!model.containsAttribute("eventType")){
         model.addAttribute("eventType", eventTypeRepos.findOne(eventTypeId));
      }
      model.addAttribute("eventTypeIntervals", EventTypeInterval.values());
      model.addAttribute("allPossibleDuties", dutyRepos.findAll());
      return "admin/eventType";
   }

   @RequestMapping(value = "/eventTypes/new", method = RequestMethod.GET)
   public String getAddEventType(Model model){
      logger.debug("getAddEventType()");

      if(!model.containsAttribute("eventType")){
         model.addAttribute("eventType", new EventType());
      }
      
      model.addAttribute("eventTypeIntervals", EventTypeInterval.values());
      model.addAttribute("allPossibleDuties", dutyRepos.findAll());
      return "admin/eventType";
   }

   @RequestMapping(value = "/duties", method = RequestMethod.GET)
   public String getDuties(Model model){
      logger.debug("getDuties()");

      final List<Duty> duties = dutyRepos.findAll();
      logger.debug("duties found: {}", duties.size());

      model.addAttribute("duties", duties);
      return "admin/duties";
   }

   @RequestMapping(value = "/duties", method = RequestMethod.POST)
   public String saveDuty(@ModelAttribute @Valid Duty duty, BindingResult result, final RedirectAttributes redirectAttributes){
      logger.debug("saveDuty()");
      final boolean alreadyExisted = duty.getId() > 0;

      if(result.hasErrors()){
         redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.duty", result);
         redirectAttributes.addFlashAttribute("duty", duty);
         
         if(alreadyExisted){
            return "redirect:/admin/duties/" + duty.getId();
         } else {
            return "redirect:/admin/duties/new";
         }
      }

      dutyService.saveOrUpdateDuty(duty);
      redirectAttributes.addFlashAttribute("msg_success", alreadyExisted ? "Duty updated!" : "Duty added!");
      return "redirect:/admin/duties";
   }

   @RequestMapping(value = "/duties/new", method = RequestMethod.GET)
   public String getNewDuty(Model model){
      logger.debug("getNewDuty()");

      if(!model.containsAttribute("duty")){
         model.addAttribute("duty", new Duty());
      }
      
      model.addAttribute("maxSortOrder", dutyRepos.findMaxSortOrder() + 1);
      return "admin/duty";
   }

   @RequestMapping(value = "/duties/{dutyId}", method = RequestMethod.GET)
   public String getEditDuty(@PathVariable Long dutyId, Model model){
      logger.debug("getEditDuty()");

      if(!model.containsAttribute("duty")){
         model.addAttribute("duty", dutyRepos.findOne(dutyId));
      }
      
      model.addAttribute("maxSortOrder", dutyRepos.findMaxSortOrder());
      return "admin/duty";
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

   @RequestMapping(value = "/people/new", method = RequestMethod.GET)
   public String getAddPerson(Model model){
      logger.debug("getAddPerson()");
      if(!model.containsAttribute("person")){
         model.addAttribute("person", new Person());
      }
      
      return "admin/person";
   }

   @RequestMapping(value = "/people/{personId}", method = RequestMethod.GET)
   public String getEditPerson(@PathVariable Long personId, Model model){
      logger.debug("getEditPerson()");
      
      if(!model.containsAttribute("person")){
         model.addAttribute("person", personRepos.findOne(personId));
      }
      
      return "admin/person";
   }


   @RequestMapping(value = "/people", method = RequestMethod.POST)
   public String savePerson(@ModelAttribute @Valid Person person, BindingResult result, final RedirectAttributes redirectAttributes){
      logger.debug("savePerson()");
      final boolean personAlreadyExisted = person.getId() > 0;

      if(result.hasErrors()){         
         redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.person", result);
         redirectAttributes.addFlashAttribute("person", person);
         
         if(personAlreadyExisted){
            return "redirect:/admin/people/" + person.getId();
         } else {
            return "redirect:/admin/people/new";
         }
      }

      personRepos.save(person);
      redirectAttributes.addFlashAttribute("msg_success", personAlreadyExisted ? "Person updated!" : "Person added!");
      return "redirect:/admin/people";
   }

   @RequestMapping(value = "/people/{personId}/duties", method = RequestMethod.GET)
   public String getManageDuties(@PathVariable Long personId, Model model){
      logger.debug("getManageDuties()");

      final Person person = personRepos.findOne(personId);

      model.addAttribute("personName", person.getNameFirst() + " " + person.getNameLast());
      model.addAttribute("person", person);
      model.addAttribute("duties", dutyRepos.findAllByOrderByNameAsc());
      return "admin/personDuties";
   }

   @RequestMapping(value = "/people/{personId}/duties", method = RequestMethod.POST)
   public String updateDuties(@PathVariable Long personId, Model model, @RequestParam MultiValueMap<String, String> parameters, final RedirectAttributes redirectAttributes){
      logger.debug("updateDuties()");

      final Person person = personRepos.findOne(personId);

      final Map<Long, Integer> dutyPrefs = new HashMap<Long, Integer>();

      for (final Iterator<Entry<String, List<String>>> iter = parameters.entrySet().iterator(); iter.hasNext();) {
         final Entry<String, List<String>> entry = iter.next();
         final String key = entry.getKey();
         final List<String> vals = entry.getValue();

         if(key != null && vals != null && vals.size() > 0 && key.startsWith("duty_")){
            final long dutyId = Long.parseLong(key.split("_")[1]);
            final int prefRanking = Integer.parseInt(vals.get(0));
            dutyPrefs.put(dutyId, prefRanking);
         }
      }

      logger.info("Duty Prefs: {}", dutyPrefs);

      boolean personUpdated = false;
      for (Map.Entry<Long, Integer> entry : dutyPrefs.entrySet()) { 
         final Long dutyId = entry.getKey();

         if(dutyId != null && dutyId >= 0){
            final Duty duty = dutyRepos.findOne(dutyId);
            final int prefRanking = entry.getValue();

            person.addOrUpdateDutyAndPreference(duty, prefRanking);
            personUpdated = true;
         }
      }

      if(personUpdated){
         personRepos.save(person);
      }

      redirectAttributes.addFlashAttribute("msg_success", "Duties updated!");
      return "redirect:/admin/people";
   }


}
