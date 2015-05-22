package com.dak.duty.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{
   public List<Person> findByDuties_Duty(Duty duty);
   public List<Person> findByIdNotIn(Collection<Long> personIds);
}
