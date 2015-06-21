package com.dak.duty.security;

import org.springframework.security.core.Authentication;

import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;

public interface IAuthenticationFacade {
   Authentication getAuthentication();
   Organisation getOrganisation();
   Person getPerson();
}
