package com.dak.duty.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.DutyNode;
import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.model.DutyPreference;
import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.PersonService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/person")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ROLE_USER')") // set in security config
public class PersonApi {

	private static final Logger logger = LoggerFactory.getLogger(PersonApi.class);

	private final PersonRepository personRepos;
	private final PersonService personService;
	private final PasswordEncoder encoder;
	private final IAuthenticationFacade authenticationFacade;
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody List<Person> getAllPeople(final Model model) {
		logger.debug("getAllPeople()");

		return this.personRepos.findAll(PersonSpecs.sameOrg().and(PersonSpecs.orderByNameLastAscNameFirstAsc()));
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody JsonResponse delete(@RequestBody Person person) {
		logger.debug("person.delete({})", person);

		person = this.personRepos.findOne(person.getId());
		person.setActive(false);
		this.personService.save(person);

		return new JsonResponse(ResponseStatus.OK, "Person " + person.getId() + " deleted");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@PreAuthorize("#p.emailAddress == authentication.name or (hasRole('ROLE_ADMIN') and #p.organisation.id == principal.person.organisation.id)")
	public @ResponseBody Person get(@PathVariable("id") @P("p") final Person person) {
		logger.debug("person.get({})", person.getId());

		return person;
	}
	
	@RequestMapping(value = "/{id}/dutyPreferences", method = RequestMethod.POST)
	@PreAuthorize("#p.emailAddress == authentication.name or (hasRole('ROLE_ADMIN') and #p.organisation.id == principal.person.organisation.id)")
	public @ResponseBody Boolean postDutyPreferences(@PathVariable("id") @P("p") final Person person, @RequestBody final List<DutyPreference> dutyPreferences) {
		logger.debug("postDutyPreferences({}, {})", person.getId(), dutyPreferences);

		personService.updateDutiesFromDutyPreference(person, dutyPreferences);
		
		return true;
	}

	@RequestMapping(value = "/{id}/upcomingDuties", method = RequestMethod.GET)
	@PreAuthorize("#p.emailAddress == authentication.name or (hasRole('ROLE_ADMIN') and #p.organisation.id == principal.person.organisation.id)")
	public @ResponseBody List<DutyNode> getUpcomingDuties(@PathVariable("id") @P("p") final Person person) {
		return this.personService.getUpcomingDuties(person);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody JsonResponse save(@RequestBody Person person) {
		logger.debug("person.save({})", person.getEmailAddress());
		
		final Boolean personAlreadyExisted = person.getId() > 0;
		
		if(!personAlreadyExisted) {
			person.setOrganisation(authenticationFacade.getOrganisation());
		}

		person = this.personService.save(person);

		return new JsonResponse(ResponseStatus.OK, "Person saved with id " + person.getId());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResponse setPassword(@RequestBody final Person person) {
		logger.debug("setPassword.save({})", person.getId());

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
