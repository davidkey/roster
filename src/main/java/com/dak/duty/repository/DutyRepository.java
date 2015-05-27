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
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder + 1 where d.sortOrder >= ?1")
   int incrementSortOrderAboveAndIncluding(Integer sortOrder);
   
   @Modifying
   @Query("update Duty d set d.sortOrder = d.sortOrder - 1 where d.sortOrder > ?1")
   int decrementSortOrderAbove(Integer sortOrder);
   
   @Query("select max(d.sortOrder) from Duty d")
   int findMaxSortOrder();
}
