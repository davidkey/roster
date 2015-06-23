package com.dak.duty.controller.admin;

import static com.dak.duty.repository.specification.PersonSpecs.orderByNameLastAscNameFirstAsc;
import static com.dak.duty.repository.specification.PersonSpecs.sameOrg;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.List;
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
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
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
   
   @Autowired
   IAuthenticationFacade authenticationFacade;

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
         if(!personRepos.findOne(person.getId()).getOrganisation().getId().equals(authenticationFacade.getOrganisation().getId())){
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
         person.setOrganisation(authenticationFacade.getOrganisation());
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

      personService.updateDutiesFromFormPost(person, parameters);

      redirectAttributes.addFlashAttribute("msg_success", "Duties updated!");
      return "redirect:/admin/people";
   }
}
