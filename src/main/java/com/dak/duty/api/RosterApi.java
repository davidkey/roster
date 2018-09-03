package com.dak.duty.api;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dak.duty.model.Event;
import com.dak.duty.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/roster")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RosterApi {

	private final EventRepository eventRepos;
	//private final IAuthenticationFacade authenticationFacade;
	
	@GetMapping
	public List<Event> getRostersAndEvents(final Principal principal) {
		log.trace("getRostersAndEvents()");
		return eventRepos.findAll();
	}
	
	
}
