package com.dak.duty.controller.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.EventService;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/user/upcomingDuties")
@PreAuthorize("hasRole('ROLE_USER')")
public class UpcomingDutiesController {

	private static final Logger logger = LoggerFactory.getLogger(UpcomingDutiesController.class);

	@Autowired
	PersonService personService;

	@Autowired
	PersonRepository personRepos;

	@Autowired
	EventRepository eventRepos;

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	EventService eventService;

	@Autowired
	IAuthenticationFacade authenticationFacade;

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public @ResponseBody int getUpcomingDutiesCount(final Principal principal) {
		UpcomingDutiesController.logger.debug("getUpcomingDutiesCount()");

		return this.personService.getUpcomingDuties(this.authenticationFacade.getPerson()).size();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getUpcomingDutiesAll(final Principal principal, final Model model) {
		UpcomingDutiesController.logger.debug("getUpcomingDutiesCount()");
		model.addAttribute("upcomingDuties", this.personService.getUpcomingDuties(this.authenticationFacade.getPerson()));
		return "user/duties";
	}

	@RequestMapping(value = "/optOut", method = RequestMethod.POST)
	public @ResponseBody JsonResponse optOut(@ModelAttribute("dutyId") final Duty duty, @ModelAttribute("eventId") final Event event,
			final Principal principal) {
		UpcomingDutiesController.logger.debug("optOut()");

		final Person person = this.authenticationFacade.getPerson();

		if (this.eventService.optPersonAndDutyOutOfEvent(person, duty, event)) {
			return new JsonResponse(ResponseStatus.OK, "Opted out.");
		} else {
			return new JsonResponse(ResponseStatus.ERROR, "Opting out failed!");
		}
	}
}
