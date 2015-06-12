package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.Duty;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long>{
   List<Duty> findAllByOrderByNameAsc();
   List<Duty> findAllByActiveTrueOrderByNameAsc();
   List<Duty> findByActiveTrue();
   List<Duty> findByActiveTrueOrderBySortOrderAsc();
   List<Duty> findByNameContainsIgnoreCaseAndActiveTrue(String name);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder >= ?1 and d.active = true")
   public Integer incrementSortOrderAboveAndIncluding(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder >= ?1 and d.id != ?2 and d.active = true")
   public Integer incrementSortOrderAboveAndIncludingExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.sortOrder >= ?1 and d.id != ?2 and d.active = true")
   public Integer decrementSortOrderAboveAndIncludingExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder > ?1 and d.active = true")
   public Integer incrementSortOrderAbove(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder > ?1 and d.id != ?2 and d.active = true")
   public Integer incrementSortOrderAboveExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.sortOrder > ?1 and d.active = true")
   public Integer decrementSortOrderAbove(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.sortOrder > ?1 and d.id != ?2 and d.active = true")
   public Integer decrementSortOrderAboveExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Query("select max(d.sortOrder) from Duty d where d.active = true")
   public Integer findMaxSortOrder();
}
