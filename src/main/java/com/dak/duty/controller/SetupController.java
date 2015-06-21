package com.dak.duty.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.form.SetupForm;
import com.dak.duty.model.Email;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.EmailService;
import com.dak.duty.service.InitialisationService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/setup")
public class SetupController {

   private static final Logger logger = LoggerFactory.getLogger(SetupController.class);

   @Autowired
   EmailService<MailgunMailMessage> emailService;
   
   @Autowired
   InitialisationService initService;
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   PersonService personService;
   
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
         if(form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())){
            bindingResult.rejectValue("confirmPassword", null, "Passwords must match!");
         }
         model.addAttribute("setupForm", form);
         return "setup/setup";
      }
      
      // create org and admin user
      initService.createOrganisationAndAdminUser(form);

      // log in as newly created user
      personService.loginAsPerson(form.getEmailAddress().trim(), form.getPassword(), request);
      
      // send welcome email
      try {
         emailService.send(new Email("admin@roster.guru", form.getEmailAddress().trim(), "Welcome to Duty Roster!", "We hope you like it!"));
      } catch (Exception e){
         logger.error("error: {}", e);
      }

      return "redirect:/admin";
   }

}
