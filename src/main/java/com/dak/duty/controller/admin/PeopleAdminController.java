package com.dak.duty.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/admin/people")
public class PeopleAdminController {

	private static final Logger logger = LoggerFactory.getLogger(PeopleAdminController.class);

	@Autowired
	PersonRepository personRepos;

	@Autowired
	PersonService personService;

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	IAuthenticationFacade authenticationFacade;

	@RequestMapping(method = RequestMethod.GET)
	public String getPeople(final Model model) {
		PeopleAdminController.logger.debug("getPeople()");

		final List<Person> people = this.personRepos
				.findAll(Specifications.where(PersonSpecs.sameOrg()).and(PersonSpecs.orderByNameLastAscNameFirstAsc()));

		PeopleAdminController.logger.debug("people found: {}", people.size());

		model.addAttribute("people", people);
		return "admin/people";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String savePerson(@ModelAttribute @Valid final Person person, final BindingResult result,
			final RedirectAttributes redirectAttributes) {
		PeopleAdminController.logger.debug("savePerson()");
		final boolean personAlreadyExisted = person.getId() > 0;

		if (personAlreadyExisted) {
			if (!this.personRepos.findOne(person.getId()).getOrganisation().getId()
					.equals(this.authenticationFacade.getOrganisation().getId())) {
				throw new RosterSecurityException("can't do that!");
			}

			final Person existingPersonRecord = this.personRepos.findOne(person.getId());
			person.setPassword(existingPersonRecord.getPassword());
		}

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.person", result);
			redirectAttributes.addFlashAttribute("person", person);

			if (personAlreadyExisted) {
				return "redirect:/admin/people/" + person.getId();
			} else {
				return "redirect:/admin/people/new";
			}
		}

		if (!personAlreadyExisted) {
			person.addRoles(this.personService.getDefaultRoles());
			person.setOrganisation(this.authenticationFacade.getOrganisation());
		}

		this.personService.save(person);
		redirectAttributes.addFlashAttribute("msg_success", personAlreadyExisted ? "Person updated!" : "Person added!");
		return "redirect:/admin/people";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getAddPerson(final Model model) {
		PeopleAdminController.logger.debug("getAddPerson()");
		if (!model.containsAttribute("person")) {
			model.addAttribute("person", new Person());
		}

		return "admin/person";
	}

	@PreAuthorize("#p.organisation.id == principal.person.organisation.id")
	@RequestMapping(value = "/{personId}", method = RequestMethod.GET)
	public String getEditPerson(final @PathVariable("personId") @P("p") Person person, final Model model) {
		PeopleAdminController.logger.debug("getEditPerson()");

		if (!model.containsAttribute("person")) {
			model.addAttribute("person", person);
		}

		return "admin/person";
	}

	@PreAuthorize("#p.organisation.id == principal.person.organisation.id")
	@RequestMapping(value = "/{personId}/duties", method = RequestMethod.GET)
	public String getManageDuties(final @PathVariable("personId") @P("p") Person person, final Model model) {
		PeopleAdminController.logger.debug("getManageDuties()");

		model.addAttribute("personName", person.getNameFirst() + " " + person.getNameLast());
		model.addAttribute("person", person);
		model.addAttribute("duties", this.dutyRepos.findAllByActiveTrueOrderByNameAsc());
		return "admin/personDuties";
	}

	@PreAuthorize("#p.organisation.id == principal.person.organisation.id")
	@RequestMapping(value = "/{personId}/duties", method = RequestMethod.POST)
	public String updateDuties(final @PathVariable("personId") @P("p") Person person, final Model model,
			@RequestParam final MultiValueMap<String, String> parameters, final RedirectAttributes redirectAttributes) {
		PeopleAdminController.logger.debug("updateDuties()");

		this.personService.updateDutiesFromFormPost(person, parameters);

		redirectAttributes.addFlashAttribute("msg_success", "Duties updated!");
		return "redirect:/admin/people";
	}
}
