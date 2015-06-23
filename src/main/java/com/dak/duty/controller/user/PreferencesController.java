package com.dak.duty.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.repository.DutyRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/user/preferences")
public class PreferencesController {
   
   private static final Logger logger = LoggerFactory.getLogger(PreferencesController.class);
   
   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   PersonService personService;
   
   @Autowired
   IAuthenticationFacade authenciationFacade;
   

   @RequestMapping(method = RequestMethod.GET)
   public String getPreferences(Model model){
      
      model.addAttribute("person", authenciationFacade.getPerson());
      model.addAttribute("duties", dutyRepos.findAllByActiveTrueOrderByNameAsc());
      
      return "user/preferences";
   }
   
   @RequestMapping(method = RequestMethod.POST)
   public String savePreferences(Model model, @RequestParam MultiValueMap<String, String> parameters, final RedirectAttributes redirectAttributes){
      logger.debug("savePreferences()");
      
      personService.updateDutiesFromFormPost(authenciationFacade.getPerson(), parameters);
      redirectAttributes.addFlashAttribute("msg_success", "Duties updated!");
      return "redirect:/user/preferences";
   }
}
