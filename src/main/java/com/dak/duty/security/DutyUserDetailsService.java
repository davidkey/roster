package com.dak.duty.security;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.dak.duty.model.enums.Role;
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
		if(personRoles == null || personRoles.isEmpty()) {
			return Collections.emptyList();
		}
		
		return personRoles.stream().map(PersonRole::getRole).map(Role::toString).distinct().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

}
