package com.dak.duty.security;

import java.util.Collection;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;

import com.dak.duty.model.Person;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User{
   private static final long serialVersionUID = -8162607256674356323L;
   
   @Getter
   private Person person;
   
   public CustomUserDetails(Person person, Collection<? extends GrantedAuthority> authorities) {
      super(person.getEmailAddress(), person.getPassword(), person.getActive(), true, true, true, authorities);

      this.person = person;
   }

   public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
     super(username, password, authorities);
   }

}
