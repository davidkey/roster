package com.dak.duty.repository.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;

public class PersonSpecs extends AbstractSpecs {
   
   public static Specification<Person> emailEquals(final String emailAddress){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.equal(root.<Boolean>get("emailAddress"), emailAddress);
         }
      };
   }

   public static Specification<Person> isActive(){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.isTrue(root.<Boolean>get("active"));
         }
      };
   }
   
   public static Specification<Person> isNotActive(){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.isFalse(root.<Boolean>get("active"));
         }
      };
   }

   public static Specification<Person> sameOrg(){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.equal(root.<Organisation>get("organisation"), getAuthorizedPerson().getOrganisation());
         }
      };
   }
}
