package com.dak.duty.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public Organisation getOrganisation() {
		return this.getPerson().getOrganisation(); // this could be null - problem?
	}

	@Override
	public Person getPerson() {
		Person p = new Person();

		try {
			p = ((CustomUserDetails) this.getAuthentication().getPrincipal()).getPerson();
		} catch (final ClassCastException cce) {
			// do nothing - give them a blank person
		}

		return p;
	}

}
