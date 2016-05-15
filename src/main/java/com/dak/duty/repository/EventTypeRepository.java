package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {

	@Override
	@Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.id = ?1")
	EventType findOne(final Long id);

	@Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.name = ?1")
	EventType findByName(final String name);

	@Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.interval = ?1")
	List<EventType> findByInterval(EventTypeInterval eti);

	@Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.active = true")
	List<EventType> findByActiveTrue();

	@Query("select et from EventType et where et.organisation = ?#{principal.person.organisation} "
			+ "and et.id not in (select v.eventType from Event v where v.organisation = ?#{principal.person.organisation})")
	List<EventType> getEventTypesWithNoEvents();

	@Query("select e from EventType e where e.organisation = ?#{principal.person.organisation} and e.active = true "
			+ "and lower(e.name) like '%' || lower(:name) || '%'")
	List<EventType> findByNameContainsIgnoreCaseAndActiveTrue(@Param("name") String name);

	@Override
	@Query("select e from EventType e where e.organisation = ?#{principal.person.organisation}")
	List<EventType> findAll();
}
