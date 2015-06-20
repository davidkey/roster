package com.dak.duty.repository.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.dak.duty.model.Event;

public class EventSpecs {
   public static Specification<Event> isApproved(){
      
      return new Specification<Event>(){
         
         @Override
         public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.isTrue(root.<Boolean>get("approved"));
         }
         
      };
   }
   
   public static Specification<Event> isNotApproved(){
      
      return new Specification<Event>(){
         
         @Override
         public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.isFalse(root.<Boolean>get("approved"));
         }
         
      };
   }
}
