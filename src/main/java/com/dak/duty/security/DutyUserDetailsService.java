package com.dak.duty.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Person;
import com.dak.duty.model.PersonRole;
import com.dak.duty.repository.PersonRepository;

@Service
public class DutyUserDetailsService implements UserDetailsService {

	@Autowired
	PersonRepository personRepos;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final Person person = this.personRepos.findByEmailAddress(username);
		final List<GrantedAuthority> authorities = this.buildUserAuthority(person.getRoles());

		return this.buildUserForAuthentication(person, authorities);
	}

	// converts Person to org.springframework.security.core.userdetails.User
	private User buildUserForAuthentication(final Person person, final List<GrantedAuthority> authorities) {
		return new CustomUserDetails(person, authorities);
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
