package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Person;

@Repository
@RepositoryDefinition(domainClass = Person.class, idClass = Long.class)
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
   public Person findByEmailAddress(String emailAddress);
}
