package com.dak.duty.controller.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.dak.duty.security.AuthenticationFacade;
import com.dak.duty.service.EventService;
import com.dak.duty.service.PersonService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user/upcomingDuties")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class UpcomingDutiesController {

	private static final Logger logger = LoggerFactory.getLogger(UpcomingDutiesController.class);

	private final PersonService personService;
	private final EventService eventService;
	private final AuthenticationFacade authenticationFacade;

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public @ResponseBody int getUpcomingDutiesCount(final Principal principal) {
		logger.debug("getUpcomingDutiesCount()");

		return this.personService.getUpcomingDuties(this.authenticationFacade.getPerson().get()).size();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getUpcomingDutiesAll(final Principal principal, final Model model) {
		logger.debug("getUpcomingDutiesCount()");
		model.addAttribute("upcomingDuties", this.personService.getUpcomingDuties(this.authenticationFacade.getPerson().get()));
		return "user/duties";
	}

	@RequestMapping(value = "/optOut", method = RequestMethod.POST)
	public @ResponseBody JsonResponse optOut(@ModelAttribute("dutyId") final Duty duty, @ModelAttribute("eventId") final Event event,
			final Principal principal) {
		logger.debug("optOut()");

		final Person person = this.authenticationFacade.getPerson().get();

		if (this.eventService.optPersonAndDutyOutOfEvent(person, duty, event)) {
			return new JsonResponse(ResponseStatus.OK, "Opted out.");
		} else {
			return new JsonResponse(ResponseStatus.ERROR, "Opting out failed!");
		}
	}
}
