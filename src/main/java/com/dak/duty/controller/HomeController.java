package com.dak.duty.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.PersonRepository;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	DutyRepository dutyRepos;
	
	@Autowired
	PersonRepository personRepos;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@Transactional
    public String testThis(Locale locale, Model model) {
	   Duty dutyPreaching = new Duty();
	   dutyPreaching.setName("Preaching");
	   dutyPreaching.setDescription("Preaching / boring stuff");
	   dutyRepos.saveAndFlush(dutyPreaching);
	   
	   Duty dutySinging = new Duty();
	   dutySinging.setName("Singing");
	   dutySinging.setDescription("Singing and stuff");
       dutyRepos.saveAndFlush(dutySinging);
       
       Person p = new Person();
       p.setNameFirst("David");
       p.setNameLast("Key");
       p.setEmailAddress("davidkey@gmail.com");
       p.setActive(true);
       personRepos.saveAndFlush(p);
       
       PersonDuty pd = new PersonDuty();
       pd.setDuty(dutyPreaching);
       pd.setPreference(5);
       
       p.addPersonDuty(pd);
       personRepos.saveAndFlush(p);
       
       
	   
	   
	   return "done";
	}
	
}
