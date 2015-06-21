package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Person;

@Repository
@RepositoryDefinition(domainClass = Person.class, idClass = Long.class)
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
   
   @Query("select p from Person p where p.organisation = ?#{principal.person.organisation} and p.id = ?1")
   public Person findOne(final Long id);
   
   public Person findByEmailAddress(String emailAddress);
}
