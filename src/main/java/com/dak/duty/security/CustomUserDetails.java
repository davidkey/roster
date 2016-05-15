package com.dak.duty.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.dak.duty.model.Person;

import lombok.Getter;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = -8162607256674356323L;

	@Getter
	private Person person;

	public CustomUserDetails(final Person person, final Collection<? extends GrantedAuthority> authorities) {
		super(person.getEmailAddress(), person.getPassword(), person.getActive(), true, true, true, authorities);

		this.person = person;
	}

	public CustomUserDetails(final String username, final String password, final Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

}
