package com.dak.duty.config.jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Builder
@ToString
public class JwtUser implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final String username;
	private final String fullName;
	private final Collection<? extends GrantedAuthority> authorities;

	@Getter
	private final Person person;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public static JwtUser fromClaims(final Claims claims, final PersonRepository personRepos) {
		@SuppressWarnings("unchecked")
		final List<String> roles = claims.get("roles", List.class);
		final String username = claims.get("jti", String.class);
		final String fullName = claims.get("fullName", String.class);
		final Person person = personRepos.findByEmailAddress(username);

		return JwtUser.builder()
				.username(username)
				.fullName(fullName)
				.person(person)
				.authorities(roles.stream().map(JwtUser::replaceDashesWithUnderscores).map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
				.build();
	}
	
	private static String replaceDashesWithUnderscores(final String input) {
		if(input == null) {
			return null;
		}
		
		return input.replaceAll("-", "_");
	}
	
	public String getFullName() {
		return fullName;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}