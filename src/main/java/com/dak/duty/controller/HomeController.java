package com.dak.duty.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dak.duty.service.InitialisationService;

@Controller
public class HomeController {

   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   @Autowired
   InitialisationService initService;

   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String home(Locale locale, Model model) {
      if(!initService.initSetupComplete()){
         logger.debug("creating default admin user");
         initService.createDefaultAdminUser("davidkey@gmail.com", "password");
      }

      logger.info("Welcome home! The client locale is {}.", locale);

      Date date = new Date();
      DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

      String formattedDate = dateFormat.format(date);

      model.addAttribute("serverTime", formattedDate );

      return "home";
   }

   @RequestMapping(value = "/login", method = RequestMethod.GET)
   public String getLogin(
         @RequestParam(value = "error", required = false) String error, 
         @RequestParam(value = "logout", required = false) String logout,
         Model model, HttpServletRequest request){

      if (error != null) {
         model.addAttribute("error", "Invalid username and password!");
      }
      
      return "login";
   }
}
