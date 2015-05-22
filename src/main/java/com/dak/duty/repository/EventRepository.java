package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Event;
import com.dak.duty.model.EventType;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{
   //public Event findTopByOrderByDateEventDesc()//;(final EventType et);
   
   // commenting out because this *only* returns rows of the same event type. 
   // @Query("select e from Event e where e.eventType = ?1 and e.dateEvent = (select max(e.dateEvent) from Event e where e.eventType = ?1)")
   // instead, we want to show ALL rows from the date this event type last occured
   // example: we're creating roster for Sunday AM - we want to pull last Sunday PM and last Sunday AM
   @Query("select e from Event e where e.eventType = ?1 and e.dateEvent = (select max(e.dateEvent) from Event e where e.eventType = ?1)")
   public List<Event> findMostRecentEventsByEventType(final EventType et);

}
