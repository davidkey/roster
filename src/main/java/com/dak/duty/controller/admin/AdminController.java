package com.dak.duty.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.service.InitialisationService;

@Controller
@RequestMapping("/admin")
public class AdminController {

   private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

   @Autowired
   InitialisationService initService;
   
   @RequestMapping(method = RequestMethod.GET)
   public String getAdminHome(Model model){
      logger.debug("getAdminHome()");
      return "admin/admin";
   }

   @RequestMapping(value = "/settings", method = RequestMethod.GET)
   public String getSettings(Model model){
      logger.debug("getSettings()");
      return "admin/settings";
   }

   @RequestMapping(value = "/about", method = RequestMethod.GET)
   public String getAbout(Model model){
      logger.debug("getAbout()");
      return "admin/about";
   }
   
   @RequestMapping("/init")
   public @ResponseBody String initData(){
      initService.populateDefaultData();
      return "data init'd";
   }

}
