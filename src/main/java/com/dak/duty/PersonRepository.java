package com.dak.duty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
