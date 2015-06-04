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
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder >= ?1")
   public Integer incrementSortOrderAboveAndIncluding(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder >= ?1 and d.id != ?2")
   public Integer incrementSortOrderAboveAndIncludingExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder > ?1")
   public Integer incrementSortOrderAbove(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder > ?1 and d.id != ?2")
   public Integer incrementSortOrderAboveExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.sortOrder > ?1")
   public Integer decrementSortOrderAbove(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.sortOrder > ?1 and d.id != ?2")
   public Integer decrementSortOrderAboveExcludingDutyId(Integer sortOrder, Long dutyId);
   
   @Query("select max(d.sortOrder) from Duty d")
   public Integer findMaxSortOrder();
}
