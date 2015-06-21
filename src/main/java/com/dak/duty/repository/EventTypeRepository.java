package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long>{
   
   @Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.id = ?1")
   public EventType findOne(final Long id);
   
   @Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.name = ?1")
   public EventType findByName(final String name);
   
   @Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.interval = ?1")
   public List<EventType> findByInterval(EventTypeInterval eti);
   
   @Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.active = true")
   public List<EventType> findByActiveTrue();
   
   @Query("select et from EventType et where et.organisation = ?#{principal.person.organisation} "
         + "and et.id not in (select v.eventType from Event v where v.organisation = ?#{principal.person.organisation})")
   public List<EventType> getEventTypesWithNoEvents();
   
   @Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.active = true "
         + "and lower(e.name) like '%' || lower(:name) || '%'")
   public List<EventType> findByNameContainsIgnoreCaseAndActiveTrue(@Param("name") String name);
   
   @Query("select e from EventType e where e.organisation = ?#{principal.person.organisation}")
   public List<EventType> findAll();
}
