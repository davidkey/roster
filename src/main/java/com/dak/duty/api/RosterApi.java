package com.dak.duty.api;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dak.duty.model.Event;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.service.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/roster")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RosterApi {

	private final EventRepository eventRepos;
	private final EventService eventService;
	//private final IAuthenticationFacade authenticationFacade;
	
	@GetMapping
	public List<Event> getRostersAndEvents(final Principal principal) {
		log.trace("getRostersAndEvents()");
		
		// need to filter out org and roster from this dataset
		final List<Event> allEvents = eventRepos.findAll();
		
		for(Event e : allEvents) {
			e.setOrganisation(null);
			e.setRoster(null);
			e.getEventType().setDuties(null);
			e.getEventType().setOrganisation(null);
		}
		
		return allEvents; 
	} 
	
	@PostMapping("/generate")
	public String generateRosters() {
		log.debug("generateRosters()");

		final int numGenerated = this.eventService.createAndSaveEventsForNextMonth();
		return numGenerated + " rosters generated!";
	}
	
	@PostMapping("/generateMissing")
	public String generateMissingRosters() {
		log.debug("generateMissingRosters()");

		final int numGenerated = this.eventService.createAndSaveMissingEvents();
		return numGenerated + " missing rosters generated!";
	}
	
	@PostMapping("/approveAllFullyPopulated")
	public String approveAllFullyPopulated() {
		log.debug("approveAllFullyPopulated()");

		final List<Event> events = this.eventRepos.findByApproved(false);
		final List<Event> eventsToApprove = new ArrayList<>();

		for (final Event e : events) {
			if (e.isRosterFullyPopulated()) {
				e.setApproved(true);
				eventsToApprove.add(e);
			}
		}

		this.eventRepos.saveAll(eventsToApprove);

		return eventsToApprove.size() + " rosters approved!";
	}

	@PostMapping("/approveAll")
	public String approveAll() {
		log.debug("approveAll()");

		final int numAffected = this.eventService.approveAllRosters();

		return numAffected + " rosters approved!";
	}

	@PostMapping("/unapproveAll")
	public String unapproveAll() {
		log.debug("unapproveAll()");

		final int numAffected = this.eventService.unApproveAllRosters();

		return numAffected + " rosters unapproved!";
	}

	@PostMapping("/fillEmptySlots")
	public String fillEmptySlots() {
		log.debug("fillEmptySlots()");

		final int numAffected = this.eventService.fillEmptySlots();

		return numAffected + " empty slots filled!";
	}
}
