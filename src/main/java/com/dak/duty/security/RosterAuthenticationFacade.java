package com.dak.duty.security;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;

@Component
public class RosterAuthenticationFacade implements AuthenticationFacade {

	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public Optional<Organisation> getOrganisation() {
		final Optional<Person> person = this.getPerson();
		
		if(person.isPresent()) {
			return Optional.ofNullable(person.get().getOrganisation());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Person> getPerson() {
		try {
			return Optional.ofNullable(((CustomUserDetails) this.getAuthentication().getPrincipal()).getPerson());
		} catch (final ClassCastException cce) {
			return Optional.empty();
		}
	}

}
