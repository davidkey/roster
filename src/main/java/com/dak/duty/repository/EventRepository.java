package com.dak.duty.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Event;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;

@Repository
@RepositoryDefinition(domainClass = Event.class, idClass = Long.class)
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>{
   
   // commenting out because this *only* returns rows of the same event type. 
   // @Query("select e from Event e where e.eventType = ?1 and e.dateEvent = (select max(e.dateEvent) from Event e where e.eventType = ?1)")
   // instead, we want to show ALL rows from the date this event type last occurred
   // example: we're creating roster for Sunday AM - we want to pull last Sunday PM and last Sunday AM
   @Query("select e from Event e where e.organisation = ?#{principal.person.organisation} and e.eventType = ?1 and e.dateEvent = (select max(e.dateEvent) from Event e where e.eventType = ?1)")
   public List<Event> findMostRecentEventsByEventType(final EventType et);
   
   @Query("select max(e.dateEvent) from Event e where e.organisation = ?#{principal.person.organisation}")
   public Date findMaxEventDate();
   
   @Modifying
   @Query("update Event e set e.approved = ?1 where e.approved != ?1 and e.organisation = ?#{principal.person.organisation}")
   public Integer setApprovedStatusOnAllEvents(boolean approved);
   
   @Query("select e from Event e where e.approved = ?1 and e.organisation = ?#{principal.person.organisation}")
   public List<Event> findByApproved(Boolean approved);
   
   @Query("select e from Event e where e.organisation = ?#{principal.person.organisation} order by e.dateEvent desc")
   public List<Event> findAllByOrderByDateEventDesc();
   
   @Query("select e from Event e where e.organisation = ?#{principal.person.organisation} and e.dateEvent >= ?1 and e.dateEvent <= ?2")
   public List<Event> findEventsByDateBetween(final Date startDate, final Date endDate);
   
   @Query("select e from Event e where e.organisation = ?#{principal.person.organisation} and e.dateEvent >= ?1")
   public List<Event> findAllByDateEventGreaterThanEqual(final Date d);
   
 //  @Query("select e from Event e where e.organisation = ?#{principal.person.organisation} and e.roster.person = ?1 and e.dateEvent >= ?2")
   public List<Event> findAllByRoster_PersonAndDateEventGreaterThanEqual(Person person, Date d);
   
   //@Query("select e from Event e where e.organisation = ?#{principal.person.organisation} and e.roster.person = ?1 and e.dateEvent >= ?2 "
 //        + "order by e.dateEvent asc")
   public List<Event> findAllByRoster_PersonAndDateEventGreaterThanEqualOrderByDateEventAsc(Person person, Date d);
   
   @Query("select e from Event e where e.organisation = ?#{principal.person.organisation}")
   public List<Event> findAll();
}
