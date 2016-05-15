package com.dak.duty.security;

import org.springframework.security.core.Authentication;

import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;

public interface IAuthenticationFacade {
	Authentication getAuthentication();

	Organisation getOrganisation();

	/**
	 * Caution: Be careful with this method. SecurityContext Person entity is NOT updated after login, so any changes
	 * (preferences, duties, etc) won't be updated in this particular instance. <br/>
	 * <br/>
	 * You'll want to *refresh* the Person from persistence layer if you need up-to-date data:<br/>
	 * Example: personRepos.findOne(authenciationFacade.getPerson().getId())
	 * @return Person
	 */
	Person getPerson();
}
