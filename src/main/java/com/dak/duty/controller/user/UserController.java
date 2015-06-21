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

import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
   
   private static final Logger logger = LoggerFactory.getLogger(UserController.class);
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   PersonService personService;
   
   @Autowired
   IAuthenticationFacade authenticationFacade;
   
   @RequestMapping(method = RequestMethod.GET)
   public String getUserHome(Model model, Principal principal){
      logger.debug("getUserHome");
      final Person p = authenticationFacade.getPerson();
      
      model.addAttribute("personName", p.getNameFirst() + " " + p.getNameLast());
      return "user/user";
   }
}
