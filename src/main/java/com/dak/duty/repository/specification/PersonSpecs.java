package com.dak.duty.repository.specification;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;
import com.dak.duty.model.enums.Role;

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
   
   public static Specification<Person> hasDuty(final Duty duty){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.equal(root.join("duties").<Duty>get("duty"), duty);
         }
      };
   }
   
   public static Specification<Person> hasRole(final Role role){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.equal(root.join("roles").<Duty>get("role"), role);
         }
      };
   }
   
   public static Specification<Person> idNotIn(final Collection<Long> personIds){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            final Path<Long> id = root.<Long> get("id");
            return id.in(personIds).not();
         }
      };
   }
   
   public static Specification<Person> orderByNameLastAscNameFirstAsc (){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            query.orderBy(
                  cb.asc(root.<String>get("nameLast")),
                  cb.asc(root.<String>get("nameFirst"))
                );
            return null;
         }
      };
   }
   
   public static Specification<Person> nameLastLike(final String name){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            final String likePattern = getLikePattern(name);
            return cb.like(cb.lower(root.<String>get("nameLast")), likePattern);
         }
      };
   }

   public static Specification<Person> nameFirstLike(final String name){
      return new Specification<Person>(){
         @Override
         public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            final String likePattern = getLikePattern(name);
            return cb.like(cb.lower(root.<String>get("nameFirst")), likePattern);
         }
      };
   }
}
