package com.dak.duty.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dak.duty.model.Event;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.service.EventService;
import com.dak.duty.service.IntervalService;
import com.dak.duty.service.container.EventCalendarNode;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/event")
@PreAuthorize("hasRole('ROLE_ADMIN')") 
@RequiredArgsConstructor
@Slf4j
public class EventApi {
	private final EventService eventService;
	private final IntervalService intervalService;

	@RequestMapping(value = "/{year}/{month}/json", method = RequestMethod.GET)
	public @ResponseBody List<EventCalendarNode> getEventCalendarItems(@PathVariable("year") final Integer year,
			@PathVariable("month") final Integer month) {
		log.info("getEventCalendarItems({}, {})", year, month);
		
		return this.eventService.getEventCalendarNodesForMonth(LocalDate.of(year, month, 1));
	}

	@RequestMapping(value = "/all/json", method = RequestMethod.GET)
	public @ResponseBody List<EventCalendarNode> getFutureEventCalendarItems() {
		return this.eventService
				.getAllFutureEventCalendarNodes(this.intervalService.getFirstDayOfMonth(this.intervalService.getCurrentSystemDate()));
	}

	@RequestMapping(value = "/current/json", method = RequestMethod.GET)
	public @ResponseBody List<EventCalendarNode> getCurrentEventCalendarItems() {
		return this.eventService.getEventCalendarNodesForMonth(this.intervalService.getCurrentSystemDate());
	}
	
	@PreAuthorize("#e.organisation.id == principal.person.organisation.id AND hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
	public EventAndRoster getEventAndRoster(@PathVariable("eventId") @P("e") final Event event) {
		log.debug("getEventAndRoster({})", event);

		final List<EventRosterItem> sortedRoster = this.eventService.getSortedRosterIncludingEmptySlots(event);

		//model.addAttribute("event", event);
		//model.addAttribute("roster", sortedRoster);
		return EventAndRoster.builder().roster(sortedRoster).event(event).build();
	}
	
	@Builder
	@Data
	private static class EventAndRoster {
		private final List<EventRosterItem> roster;
		private final Event event;
	}

}
