package com.dak.duty.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Person;
import com.dak.duty.model.enums.Role;

@Repository
@RepositoryDefinition(domainClass = Person.class, idClass = Long.class)
public interface PersonRepository extends JpaRepository<Person, Long> {
   public List<Person> findByActiveTrue();
   public List<Person> findByActiveTrueAndDuties_Duty(Duty duty);
   public List<Person> findByActiveTrueAndIdNotIn(Collection<Long> personIds);
   public List<Person> findAllByOrderByNameLastAscNameFirstAsc();
   public Person findByEmailAddress(String emailAddress);
   public List<Person> findByRoles_Role(Role role);
   public List<Person> findByNameLastContainingIgnoreCaseOrNameFirstContainingIgnoreCase(String nameLast, String nameFirst);
}
