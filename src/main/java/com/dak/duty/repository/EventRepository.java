package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

}
