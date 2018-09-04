package com.dak.duty.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dak.duty.form.SetupForm;
import com.dak.duty.model.AuthRequest;
import com.dak.duty.model.Email;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.service.EmailService;
import com.dak.duty.service.InitialisationService;
import com.dak.duty.service.PersonService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@PreAuthorize("isAnonymous()")
@Slf4j
public class SetupApi {
	private final EmailService<MailgunMailMessage> emailService;
	private final InitialisationService initService;
	private final PersonService personService;
	
	@PostMapping
	public HttpEntity<String> postSetup(@RequestBody @Valid final SetupForm form, final HttpServletRequest request) {
		log.debug("postSetup({})", form);

		// create org and admin user
		this.initService.createOrganisationAndAdminUser(form);
		
		final String jwt = this.personService.getJwtForUser(new AuthRequest(form.getEmailAddress(), form.getPassword()));

		// send welcome email
		try {
			this.emailService
					.send(new Email("admin@roster.guru", form.getEmailAddress().trim(), "Welcome to Duty Roster!", "We hope you like it!"));
		} catch (final Exception e) {
			log.error("error: {}", e);
		}

		final HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

		return new HttpEntity<String>(jwt, header);
	}
}
