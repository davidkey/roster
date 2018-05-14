package com.dak.duty.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.model.Event;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.service.EventService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/rosters")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class RosterAdminController {

	private static final Logger logger = LoggerFactory.getLogger(RosterAdminController.class);

	private final EventRepository eventRepos;
	private final EventService eventService;

	@RequestMapping(method = RequestMethod.GET)
	public String getRostersAndEvents(final Model model) {
		logger.debug("getRostersAndEvents()");

		final List<Event> events = this.eventRepos.findAll();
		logger.debug("events found: {}", events.size());

		model.addAttribute("events", events);
		return "admin/rosters";
	}

	@PreAuthorize("#e.organisation.id == principal.person.organisation.id AND hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
	public String getEventAndRoster(@PathVariable("eventId") @P("e") final Event event, final Model model) {
		logger.debug("getEventAndRoster({})", event);

		final List<EventRosterItem> sortedRoster = this.eventService.getSortedRosterIncludingEmptySlots(event);

		model.addAttribute("event", event);
		model.addAttribute("roster", sortedRoster);
		return "admin/roster";
	}

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public String generateRosters(final Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("generateRosters()");

		final int numGenerated = this.eventService.createAndSaveEventsForNextMonth();

		redirectAttributes.addFlashAttribute("msg_success", numGenerated + " rosters generated!");
		return "redirect:/admin/rosters";
	}

	@RequestMapping(value = "/generateMissing", method = RequestMethod.GET)
	public String generateMissingRosters(final Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("generateMissingRosters()");

		final int numGenerated = this.eventService.createAndSaveMissingEvents();

		redirectAttributes.addFlashAttribute("msg_success", numGenerated + " missing rosters generated!");
		return "redirect:/admin/rosters";
	}

	@RequestMapping(value = "/approveAllFullyPopulated", method = RequestMethod.GET)
	public String approveAllFullyPopulated(final Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("approveAllFullyPopulated()");

		final List<Event> events = this.eventRepos.findByApproved(false);
		final List<Event> eventsToApprove = new ArrayList<>();

		for (final Event e : events) {
			if (e.isRosterFullyPopulated()) {
				e.setApproved(true);
				eventsToApprove.add(e);
			}
		}

		this.eventRepos.saveAll(eventsToApprove);

		redirectAttributes.addFlashAttribute("msg_success", eventsToApprove.size() + " rosters approved!");
		return "redirect:/admin/rosters";
	}

	@RequestMapping(value = "/approveAll", method = RequestMethod.GET)
	public String approveAll(final Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("approveAll()");

		final int numAffected = this.eventService.approveAllRosters();

		redirectAttributes.addFlashAttribute("msg_success", numAffected + " rosters approved!");
		return "redirect:/admin/rosters";
	}

	@RequestMapping(value = "/unapproveAll", method = RequestMethod.GET)
	public String unapproveAll(final Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("unapproveAll()");

		final int numAffected = this.eventService.unApproveAllRosters();

		redirectAttributes.addFlashAttribute("msg_success", numAffected + " rosters unapproved!");
		return "redirect:/admin/rosters";
	}

	@RequestMapping(value = "/fillEmptySlots", method = RequestMethod.GET)
	public String fillEmptySlots(final Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("fillEmptySlots()");

		final int numAffected = this.eventService.fillEmptySlots();

		redirectAttributes.addFlashAttribute("msg_success", numAffected + " empty slots filled!");
		return "redirect:/admin/rosters";
	}
}
