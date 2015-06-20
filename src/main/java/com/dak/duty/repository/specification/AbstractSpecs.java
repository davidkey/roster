package com.dak.duty.repository.specification;

import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.model.Person;
import com.dak.duty.security.CustomUserDetails;

public abstract class AbstractSpecs {

   protected static Person getAuthorizedPerson(){
      return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
   }
}
