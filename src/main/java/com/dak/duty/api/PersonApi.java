package com.dak.duty.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.DutyNode;
import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.model.Person;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.IntervalService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/api/person")
// @PreAuthorize("hasRole('ROLE_USER')") // set in security context xml
public class PersonApi {

	private static final Logger logger = LoggerFactory.getLogger(PersonApi.class);

	@Autowired
	PersonRepository personRepos;

	@Autowired
	PersonService personService;

	@Autowired
	EventRepository eventRepos;

	@Autowired
	IntervalService intervalService;

	@Autowired
	BCryptPasswordEncoder encoder;

	@Autowired
	IAuthenticationFacade authenticationFacade;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody JsonResponse delete(@RequestBody Person person) {
		PersonApi.logger.debug("person.delete({})", person);

		person = this.personRepos.findOne(person.getId());
		person.setActive(false);
		this.personService.save(person);

		return new JsonResponse(ResponseStatus.OK, "Person " + person.getId() + " deleted");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@PreAuthorize("#p.emailAddress == authentication.name or (hasRole('ROLE_ADMIN') and #p.organisation.id == principal.person.organisation.id)")
	public @ResponseBody Person get(@PathVariable("id") @P("p") final Person person) {
		PersonApi.logger.debug("person.get({})", person.getId());

		return person;
	}

	@RequestMapping(value = "/{id}/upcomingDuties", method = RequestMethod.GET)
	@PreAuthorize("#p.emailAddress == authentication.name or (hasRole('ROLE_ADMIN') and #p.organisation.id == principal.person.organisation.id)")
	public @ResponseBody List<DutyNode> getUpcomingDuties(@PathVariable("id") @P("p") final Person person) {
		return this.personService.getUpcomingDuties(person);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody JsonResponse save(@RequestBody Person person) {
		PersonApi.logger.debug("person.save({})", person.getEmailAddress());

		person = this.personService.save(person);

		return new JsonResponse(ResponseStatus.OK, "Person saved with id " + person.getId());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/password", method = RequestMethod.POST)
	public @ResponseBody JsonResponse setPassword(@RequestBody final Person person) {
		PersonApi.logger.debug("setPassword.save({})", person.getId());

		Person personToUpdate = this.personRepos.findOne(person.getId());
		if (personToUpdate != null) {
			if (!this.personService.isPasswordValid(person.getPassword())) {
				return new JsonResponse(ResponseStatus.ERROR,
						"Password does not meet requirements: " + this.personService.getPasswordRequirements());
			}

			if (!personToUpdate.getOrganisation().getId().equals(this.authenticationFacade.getOrganisation().getId())) {
				throw new RosterSecurityException("can't do that");
			}

			personToUpdate.setPassword(this.encoder.encode(person.getPassword()));
			personToUpdate = this.personService.save(personToUpdate);
			return new JsonResponse(ResponseStatus.OK, "Password updated for Person " + personToUpdate.getId());
		} else {
			return new JsonResponse(ResponseStatus.ERROR, "Person not found - id " + person.getId());
		}
	}
}
