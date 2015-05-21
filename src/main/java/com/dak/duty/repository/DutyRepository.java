package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Duty;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long>{

}
