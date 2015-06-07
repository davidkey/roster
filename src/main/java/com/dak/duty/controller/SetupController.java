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
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.form.SetupForm;
import com.dak.duty.model.Email;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.EmailService;
import com.dak.duty.service.InitialisationService;

@Controller
@RequestMapping("/setup")
public class SetupController {

   private static final Logger logger = LoggerFactory.getLogger(SetupController.class);

   @Autowired
   EmailService emailService;
   
   @Autowired
   InitialisationService initService;
   
   @Autowired
   PersonRepository personRepos;
   
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
      
      // create admin user
      initService.createDefaultAdminUser(form.getEmailAddress().trim(), form.getPassword(), form.getNameLast().trim(), form.getNameFirst().trim());
      
      // log in as newly created user
      doAutoLogin(form.getEmailAddress().trim(), form.getPassword(), request);
      
      // send welcome email
      try {
         emailService.send(new Email("admin@duty.dak.rocks", form.getEmailAddress().trim(), "Welcome to Duty Roster!", "We hope you like it!"));
      } catch (Exception e){
         logger.error("error: {}", e);
      }

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
