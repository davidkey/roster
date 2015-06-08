package com.dak.duty.controller.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
   
   private static final Logger logger = LoggerFactory.getLogger(UserController.class);
   
   @Autowired
   PersonService personService;
   
   @Autowired
   PersonRepository personRepos;
   
   @RequestMapping(method = RequestMethod.GET)
   public String getUserHome(Model model, Principal principal){
      Person p = personRepos.findByEmailAddress(principal.getName());
      
      model.addAttribute("personName", p.getNameFirst() + " " + p.getNameLast());
      return "user/user";
   }
   
   @RequestMapping(value = "/upcomingDuties/count", method = RequestMethod.GET)
   public @ResponseBody int getUpcomingDutiesCount(Principal principal){
      logger.debug("getUpcomingDutiesCount()");
      
      Person p = personRepos.findByEmailAddress(principal.getName());
      return personService.getUpcomingDuties(p).size();
   }
   
}
