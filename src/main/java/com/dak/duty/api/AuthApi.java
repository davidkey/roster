package com.dak.duty.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dak.duty.model.AuthRequest;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApi {

	private final PersonService personService;
	private final PersonRepository personRepository;
	
	@GetMapping("/sup")
	public String getsup() {
		return "sup";
	}
	
	@PostMapping("/sup")
	public String sup() {
		return "sup";
	}
	
	@PostMapping("/addUser")
	public String addUser(@RequestBody AuthRequest authRequest) {
		personService.setPassword(personRepository.findByEmailAddress(authRequest.getUsername()), authRequest.getPassword());
		
		return "done";
	}

	@PostMapping(consumes="application/json")
	@PreAuthorize("permitAll()")
	public HttpEntity<String> authenticate(@RequestBody AuthRequest authRequest) {
		log.trace("authenticate({})", authRequest.getUsername());

		final String jwt = personService.getJwtForUser(authRequest);
		final HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

		return new HttpEntity<String>(jwt, header);
	}
}
