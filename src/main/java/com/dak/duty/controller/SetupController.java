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
import com.dak.duty.service.EmailService;
import com.dak.duty.service.InitialisationService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/setup")
public class SetupController {

	private static final Logger logger = LoggerFactory.getLogger(SetupController.class);

	@Autowired
	private EmailService<MailgunMailMessage> emailService;

	@Autowired
	private InitialisationService initService;

	@Autowired
	private PersonService personService;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@RequestMapping(method = RequestMethod.GET)
	public String getSetup(final Model model) {
		logger.debug("getSetup()");
		model.addAttribute("setupForm", new SetupForm());
		return "setup/setup";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String postSetup(@Valid final SetupForm form, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request) {
		logger.info("postSetup({})", form);

		if (bindingResult.hasErrors() || !form.getPassword().equals(form.getConfirmPassword())) {
			if (form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
				bindingResult.rejectValue("confirmPassword", null, "Passwords must match!");
			}
			model.addAttribute("setupForm", form);
			return "setup/setup";
		}

		// create org and admin user
		this.initService.createOrganisationAndAdminUser(form);

		// log in as newly created user
		this.personService.loginAsPerson(form.getEmailAddress().trim(), form.getPassword(), request);

		// send welcome email
		try {
			this.emailService
					.send(new Email("admin@roster.guru", form.getEmailAddress().trim(), "Welcome to Duty Roster!", "We hope you like it!"));
		} catch (final Exception e) {
			logger.error("error: {}", e);
		}

		return "redirect:/admin";
	}

}
