package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Duty;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long>{
   
   @Query("select d from Duty d where d.organisation = ?#{principal.person.organisation} order by d.name asc")
   List<Duty> findAllByOrderByNameAsc();
   
   @Query("select d from Duty d where d.organisation = ?#{principal.person.organisation} and d.active = true order by d.name asc")
   List<Duty> findAllByActiveTrueOrderByNameAsc();
   
   @Query("select d from Duty d where d.organisation = ?#{principal.person.organisation} and d.active = true")
   List<Duty> findByActiveTrue();
   
   @Query("select d from Duty d where d.organisation = ?#{principal.person.organisation} and d.active = true order by d.sortOrder asc")
   List<Duty> findByActiveTrueOrderBySortOrderAsc();
   
   @Query("select d from Duty d where d.organisation = ?#{principal.person.organisation} and lower(d.name) like '%' || lower(:name) || '%'  and d.active = true")
   List<Duty> findByNameContainsIgnoreCaseAndActiveTrue(@Param("name") String name);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder >= ?1 and d.active = true")
   public Integer incrementSortOrderAboveAndIncluding(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder >= ?1 and d.id != ?2 and d.active = true")
   public Integer incrementSortOrderAboveAndIncludingExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder >= ?1 and d.id != ?2 and d.active = true")
   public Integer decrementSortOrderAboveAndIncludingExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder > ?1 and d.active = true")
   public Integer incrementSortOrderAbove(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder > ?1 and d.id != ?2 and d.active = true")
   public Integer incrementSortOrderAboveExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder > ?1 and d.active = true")
   public Integer decrementSortOrderAbove(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.organisation = ?#{principal.person.organisation} and d.sortOrder > ?1 and d.id != ?2 and d.active = true")
   public Integer decrementSortOrderAboveExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Query("select max(d.sortOrder) from Duty d where d.organisation = ?#{principal.person.organisation} and d.active = true")
   public Integer findMaxSortOrder();
}
