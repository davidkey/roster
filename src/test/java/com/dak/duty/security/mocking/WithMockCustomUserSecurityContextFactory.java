package com.dak.duty.security.mocking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.dak.duty.model.Person;
import com.dak.duty.model.PersonRole;
import com.dak.duty.security.CustomUserDetails;
import com.dak.duty.service.InitialisationService;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUserAdmin> {

	private final InitialisationService initService;

	@Autowired
	public WithMockCustomUserSecurityContextFactory(final InitialisationService initService) {
		this.initService = initService;
	}

	@Override
	public SecurityContext createSecurityContext(final WithMockCustomUserAdmin customUser) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();

		final Person p = this.initService.getDefaultAdminUser();

		final CustomUserDetails principal = new CustomUserDetails(p, this.buildUserAuthority(p.getRoles()));
		final Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}

	private List<GrantedAuthority> buildUserAuthority(final Set<PersonRole> personRoles) {

		final Set<GrantedAuthority> setAuths = new HashSet<>();

		for (final PersonRole personRole : personRoles) {
			setAuths.add(new SimpleGrantedAuthority(personRole.getRole().toString()));
		}

		final List<GrantedAuthority> result = new ArrayList<>(setAuths);

		return result;
	}

}
