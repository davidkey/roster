package com.dak.duty.controller;

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
      //if(!initService.initSetupComplete()){
      //   return "redirect:/setup";
      //}

      return "home";
   }

   @RequestMapping(value = "/login", method = RequestMethod.GET)
   public String getLogin(@RequestParam(value = "error", required = false) String error, 
         Model model, HttpServletRequest request){

      logger.debug("getLogin() by ip {}", request.getRemoteAddr());
      
      if (error != null) {
         model.addAttribute("error", "Invalid username and password!");
      }

      return "login";
   }
   
   @RequestMapping(value = "/error", method = RequestMethod.GET)
   public String throwAnError(){
      
      throw new RuntimeException("yup, this is an error");
   }
}
