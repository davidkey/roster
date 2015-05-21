package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.EventType;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long>{
   public EventType findByName(final String name);
}
