package com.dak.duty.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.form.SetupForm;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonRole;
import com.dak.duty.repository.PersonRepository;

@Controller
@RequestMapping("/setup")
public class SetupController {

   private static final Logger logger = LoggerFactory.getLogger(SetupController.class);

   @Autowired
   PersonRepository personRepos;

   @Autowired
   BCryptPasswordEncoder encoder;
   
   @Autowired
   @Qualifier("authenticationManager")
   AuthenticationManager authenticationManager;

   @RequestMapping(method = RequestMethod.GET)
   public String getSetup(Model model){

      model.addAttribute("setupForm", new SetupForm());
      return "setup/setup";
   }

   @RequestMapping(method = RequestMethod.POST)
   public String postSetup(@Valid SetupForm form, BindingResult bindingResult, Model model, HttpServletRequest request){
      logger.info("postSetup({})", form);
      if (bindingResult.hasErrors() || !form.getPassword().equals(form.getConfirmPassword())) {
         if(!form.getPassword().equals(form.getConfirmPassword())){
            bindingResult.rejectValue("confirmPassword", null, "Passwords must match!");
         }
         model.addAttribute("setupForm", form);
         return "setup/setup";
      }

      Person person = new Person();
      person.setActive(true);
      person.setEmailAddress(form.getEmailAddress().trim());
      person.setNameFirst(form.getNameFirst().trim());
      person.setNameLast(form.getNameLast().trim());
      person.setPassword(encoder.encode(form.getPassword()));
      
      PersonRole adminRole = new PersonRole();
      adminRole.setRole("ROLE_ADMIN");
      
      PersonRole userRole = new PersonRole();
      userRole.setRole("ROLE_USER");
      
      person.addRole(adminRole);
      person.addRole(userRole);
      
      personRepos.save(person);

      doAutoLogin(person.getEmailAddress(), form.getPassword(), request);

      return "redirect:/admin";
   }

   private void doAutoLogin(String username, String password, HttpServletRequest request) {
      try {
         // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
         UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
         token.setDetails(new WebAuthenticationDetails(request));
         Authentication authentication = authenticationManager.authenticate(token);
         logger.debug("Logging in with [{}]", authentication.getPrincipal());
         SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
         SecurityContextHolder.getContext().setAuthentication(null);
         logger.error("Failure in autoLogin", e);
      }
   }
}
