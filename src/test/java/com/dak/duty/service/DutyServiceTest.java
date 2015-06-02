package com.dak.duty.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/servlet-context-test.xml"})
public class DutyServiceTest extends ServiceTest {

   @Autowired
   DutyService dutyService;

   @Autowired
   DutyRepository dutyRepos;

   @Test
   public void sortOrderTest(){
      List<Duty> duties = dutyRepos.findAll();

      assertTrue("Not enough duties found for testing!", duties.size() >= 2);

      Duty minDuty = getDutyWithMinSortOrder(), maxDuty = getDutyWithMaxSortOrder();

      assertNotNull(minDuty);
      assertNotNull(maxDuty);

      assertTrue("Sort order not populating correctly!", minDuty.getSortOrder() < maxDuty.getSortOrder());
      assertTrue("Sort order isn't sequential!", maxDuty.getSortOrder() == duties.size());

      int originalMinSortOrder = minDuty.getSortOrder();
      int originalMaxSortOrder = maxDuty.getSortOrder();
      minDuty.setSortOrder(originalMaxSortOrder);
      maxDuty.setSortOrder(originalMinSortOrder);

      dutyService.saveOrUpdateDuty(minDuty);
      dutyService.saveOrUpdateDuty(maxDuty);

      assertTrue("Sort order not updating correctly", maxDuty.getSortOrder() == originalMinSortOrder);
      assertTrue("Sort order not updating correctly", minDuty.getSortOrder() == originalMaxSortOrder);
      
      Duty newDuty = new Duty();
      newDuty.setDescription("new duty");
      newDuty.setName("new duty");
      newDuty.setSortOrder(1);
      dutyService.saveOrUpdateDuty(newDuty);

      assertTrue("Sort order not incrementing correctly", dutyRepos.findOne(maxDuty.getId()).getSortOrder() == originalMinSortOrder + 1);
      assertTrue("Sort order not incrementing correctly", dutyRepos.findOne(minDuty.getId()).getSortOrder() == originalMaxSortOrder + 1);
      
      newDuty.setSortOrder(2);
      dutyService.saveOrUpdateDuty(newDuty);
      
      assertTrue("Sort order not decrementing correctly", dutyRepos.findOne(maxDuty.getId()).getSortOrder() == originalMinSortOrder);
      assertTrue("Sort order not decrementing correctly", dutyRepos.findOne(minDuty.getId()).getSortOrder() == originalMaxSortOrder + 1);
      
   }

   private Duty getDutyWithMinSortOrder(){
      List<Duty> duties = dutyRepos.findAll();

      Duty duty = null;
      for(Duty d : duties){
         if(duty == null){
            duty = d;
         } else {
            if(duty.getSortOrder() > d.getSortOrder()){
               duty = d;
            }
         }
      }
      
      return duty;
   }
   
   private Duty getDutyWithMaxSortOrder(){
      List<Duty> duties = dutyRepos.findAll();

      Duty duty = null;
      for(Duty d : duties){
         if(duty == null){
            duty = d;
         } else {
            if(duty.getSortOrder() < d.getSortOrder()){
               duty = d;
            }
         }
      }
      
      return duty;
   }
}
