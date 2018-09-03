package com.dak.duty.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.dak.duty.config.jwt.JwtUser;
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
	
	public CustomUserDetails(final JwtUser jwtUser) {
		this(jwtUser.getPerson(), jwtUser.getAuthorities());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((person == null) ? 0 : person.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomUserDetails other = (CustomUserDetails) obj;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if(other != null && other.person != null && person.getId() == other.person.getId()) {
			return true;
		} else if (!person.equals(other.person)) {
			return false;
		}

		return true;
	}

}
