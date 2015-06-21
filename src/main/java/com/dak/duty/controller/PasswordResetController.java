package com.dak.duty.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.exception.InvalidIdException;
import com.dak.duty.form.PasswordForgotForm;
import com.dak.duty.form.PasswordResetForm;
import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/passwordReset")
public class PasswordResetController {
   
   private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
   
   @Autowired
   PersonService personService;
   
   @Autowired
   PersonRepository personRepos;
   
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String getForgotPassword(Model model, HttpServletRequest request){
      logger.debug("getForgotPassword({})", request.getRemoteAddr());
      
      model.addAttribute("passwordForgotForm", new PasswordForgotForm());
      return "passwordForgot";
   }
   
   @RequestMapping(value = "/", method = RequestMethod.POST)
   public String forgotPassword(Model model, HttpServletRequest request, @Valid PasswordForgotForm form, BindingResult bindingResult){
      logger.debug("forgotPassword({})", request.getRemoteAddr());
      
      if (bindingResult.hasErrors()) {
         model.addAttribute("passwordForgotForm", form);
         return "passwordForgot";
      }
      
      try{
         final String requestUrl = request.getRequestURL().toString();
         personService.initiatePasswordReset(form.getEmailAddress(), requestUrl);
         model.addAttribute("success", "You should be receiving an email shortly detailing with a link to reset your password.");
      } catch (InvalidIdException e){
         model.addAttribute("passwordForgotForm", form);
         model.addAttribute("error", "No account associated with that email address was found!");
      } catch (Exception e){
         model.addAttribute("passwordForgotForm", form);
         model.addAttribute("error", "An unknown error occured");
         logger.error("exception during password reset: {}", e);
      }
      
      return "passwordForgot";
   }
   
   
   @RequestMapping(value = "/{resetToken}", method = RequestMethod.GET)
   public String getResetPassword(@PathVariable("resetToken") String resetToken, Model model){
      logger.debug("resetPassword({})", resetToken);
      
      final Person person = personRepos.findByResetTokenAndResetTokenExpiresGreaterThan(resetToken, new Date());
      
      if(person == null){
         model.addAttribute("invalidToken", true);
         model.addAttribute("error", "Invalid reset token - may be expired.");
      }
      
      
      
      model.addAttribute("passwordResetForm", new PasswordResetForm());
      return "passwordReset";
   }
   
   @RequestMapping(value = "/{resetToken}", method = RequestMethod.POST)
   public String resetPassword(final @PathVariable("resetToken") String resetToken, @Valid PasswordResetForm form, 
         BindingResult bindingResult, Model model, HttpServletRequest request){
      logger.debug("resetPassword({})", resetToken);
      
      if (bindingResult.hasErrors() || !form.getPassword().equals(form.getConfirmPassword())) {
         if(form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())){
            bindingResult.rejectValue("confirmPassword", null, "Passwords must match!");
         }
         model.addAttribute("passwordResetForm", form);
         return "passwordReset";
      }
      
      final Person person = personRepos.findByResetTokenAndResetTokenExpiresGreaterThan(resetToken, new Date());
      
      if(person == null){
         model.addAttribute("passwordResetForm", form);
         model.addAttribute("error", "Invalid reset token - may be expired.");
         return "passwordReset";
      }
      
      if(!form.getEmailAddress().equals(person.getEmailAddress())){
         model.addAttribute("passwordResetForm", form);
         model.addAttribute("error", "Email address doesn't match what we have on file for this token.");
         return "passwordReset";
      }
      
      personService.setPassword(person, form.getPassword());
      if(personService.loginAsPerson(person.getEmailAddress(), form.getPassword(), request)){
         personService.clearResetToken(person);
      }
      
      return "redirect:/user";
   }
}
