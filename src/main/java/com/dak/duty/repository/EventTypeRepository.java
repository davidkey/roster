package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long>{
   public EventType findByName(final String name);
   public List<EventType> findByInterval(EventTypeInterval eti);
}
