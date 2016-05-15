package com.dak.duty.repository.specification;

import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.model.Person;
import com.dak.duty.security.CustomUserDetails;

public abstract class AbstractSpecs {

	protected static Person getAuthorizedPerson() {
		Person person = new Person();

		try {
			person = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
		} catch (final ClassCastException cce) {
			// nothing
		}

		return person;
	}

	protected static String getLikePattern(final String searchTerm) {
		final StringBuilder pattern = new StringBuilder();
		pattern.append("%");
		pattern.append(searchTerm.toLowerCase());
		pattern.append("%");
		return pattern.toString();
	}
}
