package com.dak.duty.api;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dak.duty.config.jwt.JwtUser;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.CustomUserDetails;
import com.dak.duty.service.InitialisationService;
import com.dak.duty.service.PersonService;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApi {

	private final PersonService personService;
	private final InitialisationService initService;
	private final PersonRepository personRepository;

	@Value("${jwt.signingKey}")
	private String signingKey;
	
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

		final Authentication auth;
		try {
			auth = personService.attemptAuthentication(authRequest.getUsername(), authRequest.getPassword());
		} catch (AuthenticationException ae) {
			log.debug("auth failed for user {}", authRequest.getUsername(), ae);
			throw new RuntimeException("auth failed for user " + authRequest.getUsername());
		}


		//		if(!ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.toString(), authRequest.password)) {
		//			throw new AuthenticationFailureException();
		//		}

		Map<String, Object> additionalClaims = new HashMap<>();

		final CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
		
		additionalClaims.put("roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
		additionalClaims.put("fullName", user.getPerson().getNameFirst() + " " + user.getPerson().getNameLast());
		additionalClaims.put("orgId", user.getPerson().getOrganisation().getId());
		additionalClaims.put("orgName", user.getPerson().getOrganisation().getName());
		//additionalClaims.put("fullName", username);

		JwtBuilder builder = Jwts.builder().setId(authRequest.getUsername())
				.setIssuedAt(new Date())
				.setSubject("roster")
				.setIssuer("roster.guru")
				.addClaims(additionalClaims)
				.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 60 * 12)) // 12 hours
				//.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 6)) // 6 minutes
				.signWith(SignatureAlgorithm.HS256, signingKey.getBytes(Charset.forName("UTF-8")));

		final String jwt = builder.compact();

		final HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

		return new HttpEntity<String>(jwt, header);
	}

	@Data
	static class AuthRequest{
		private String username;
		private String password;
	}

}
