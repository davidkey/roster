package com.dak.duty.repository.specification;

import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.model.Person;
import com.dak.duty.security.CustomUserDetails;

public abstract class AbstractSpecs {

   protected static Person getAuthorizedPerson(){
      // TODO: should really be in try{} but i want to see the errors for now...
      return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
   }
   
   protected static String getLikePattern(final String searchTerm) {
      StringBuilder pattern = new StringBuilder();
      pattern.append("%");
      pattern.append(searchTerm.toLowerCase());
      pattern.append("%");
      return pattern.toString();
  }
}
