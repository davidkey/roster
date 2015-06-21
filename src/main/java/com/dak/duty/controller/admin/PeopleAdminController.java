package com.dak.duty.controller.admin;

import static com.dak.duty.repository.specification.PersonSpecs.orderByNameLastAscNameFirstAsc;
import static com.dak.duty.repository.specification.PersonSpecs.sameOrg;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/admin/people")
public class PeopleAdminController {
   
   private static final Logger logger = LoggerFactory.getLogger(PeopleAdminController.class);
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   PersonService personService;
   
   @Autowired
   DutyRepository dutyRepos;

   @RequestMapping(method = RequestMethod.GET)
   public String getPeople(Model model){
      logger.debug("getPeople()");

      final List<Person> people = personRepos.findAll(where(sameOrg()).and(orderByNameLastAscNameFirstAsc()));
      
      logger.debug("people found: {}", people.size());

      model.addAttribute("people", people);
      return "admin/people";
   }
   
   @RequestMapping(method = RequestMethod.POST)
   public String savePerson(@ModelAttribute @Valid Person person, BindingResult result, final RedirectAttributes redirectAttributes){
      logger.debug("savePerson()");
      final boolean personAlreadyExisted = person.getId() > 0;
      
      if(personAlreadyExisted){
         if(!personRepos.findOne(person.getId()).getOrganisation().getId().equals(personService.getAuthenticatedPerson().getOrganisation().getId())){
            throw new RosterSecurityException("can't do that!");
         }
      }

      if(result.hasErrors()){         
         redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.person", result);
         redirectAttributes.addFlashAttribute("person", person);
         
         if(personAlreadyExisted){
            return "redirect:/admin/people/" + person.getId();
         } else {
            return "redirect:/admin/people/new";
         }
      }
      
      if(!personAlreadyExisted){
         person.addRoles(personService.getDefaultRoles());
         person.setOrganisation(personService.getAuthenticatedPerson().getOrganisation());
      }
    
      
      personService.save(person);
      redirectAttributes.addFlashAttribute("msg_success", personAlreadyExisted ? "Person updated!" : "Person added!");
      return "redirect:/admin/people";
   }
   
   @RequestMapping(value = "/new", method = RequestMethod.GET)
   public String getAddPerson(Model model){
      logger.debug("getAddPerson()");
      if(!model.containsAttribute("person")){
         model.addAttribute("person", new Person());
      }
      
      return "admin/person";
   }

   @PreAuthorize("#p.organisation.id == principal.person.organisation.id")
   @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
   public String getEditPerson(final @PathVariable("personId") @P("p") Person person, Model model){
      logger.debug("getEditPerson()");
      
      if(!model.containsAttribute("person")){
         model.addAttribute("person", person);
      }
      
      return "admin/person";
   }

   @PreAuthorize("#p.organisation.id == principal.person.organisation.id")
   @RequestMapping(value = "/{personId}/duties", method = RequestMethod.GET)
   public String getManageDuties(final @PathVariable("personId") @P("p") Person person, Model model){
      logger.debug("getManageDuties()");

      model.addAttribute("personName", person.getNameFirst() + " " + person.getNameLast());
      model.addAttribute("person", person);
      model.addAttribute("duties", dutyRepos.findAllByActiveTrueOrderByNameAsc());
      return "admin/personDuties";
   }

   @PreAuthorize("#p.organisation.id == principal.person.organisation.id")
   @RequestMapping(value = "/{personId}/duties", method = RequestMethod.POST)
   public String updateDuties(final @PathVariable("personId") @P("p") Person person, Model model, @RequestParam MultiValueMap<String, String> parameters, final RedirectAttributes redirectAttributes){
      logger.debug("updateDuties()");

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
         personService.save(person);
      }

      redirectAttributes.addFlashAttribute("msg_success", "Duties updated!");
      return "redirect:/admin/people";
   }
}
