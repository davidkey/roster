package com.dak.duty.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.exception.SortOrderException;
import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.OrganisationRepository;
import com.dak.duty.service.container.SortOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/servlet-context-test.xml"})
public class DutyServiceTest extends ServiceTest {

   @Autowired
   DutyService dutyService;

   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   OrganisationRepository orgRepos;
   
   @Test
   public void sortOrderCountShouldMatchDutyCount(){
      assertTrue("sort order count doesn't match duty count!", dutyService.getSortOrders().size() == dutyRepos.findAll().size());
   }

   @Test(expected = com.dak.duty.exception.SortOrderException.class)
   public void updateSortOrderShouldNotAllowIncompleteInput() throws SortOrderException{
      final List<SortOrder> sortOrders = dutyService.getSortOrders();

      assertFalse("sortOrders shouldn't be null - data not staged correctly?", sortOrders == null);
      assertFalse("sortOrders shoudn't be empty - data not staged correctly?", sortOrders.isEmpty());

      sortOrders.remove(0);

      dutyService.updateSortOrder(sortOrders);
   }

   @Test
   public void testOverallSortOrder(){
      List<Duty> duties = dutyRepos.findByActiveTrue();

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
      newDuty.setOrganisation(orgRepos.findAll().get(0));
      dutyService.saveOrUpdateDuty(newDuty);

      assertTrue("Sort order not incrementing correctly", dutyRepos.findOne(maxDuty.getId()).getSortOrder() == originalMinSortOrder + 1);
      assertTrue("Sort order not incrementing correctly", dutyRepos.findOne(minDuty.getId()).getSortOrder() == originalMaxSortOrder + 1);

      newDuty.setSortOrder(2);
      dutyService.saveOrUpdateDuty(newDuty);

      assertTrue("Sort order not decrementing correctly", dutyRepos.findOne(maxDuty.getId()).getSortOrder() == originalMinSortOrder);
      assertTrue("Sort order not decrementing correctly", dutyRepos.findOne(minDuty.getId()).getSortOrder() == originalMaxSortOrder + 1);
   }

   @Test
   public void testSortOrderInSequence(){
      List<Duty> activeDuties = dutyRepos.findByActiveTrue();

      assertNotNull(activeDuties);
      assertTrue("Not enough test data!", activeDuties.size() > 1);
      assertTrue("Sort orders not sequencing correctly!", areSortOrdersInSequence(activeDuties));
   }

   @Test
   public void testBrokenSequenceVerification(){
      Duty duty = new Duty();
      duty.setName("testBrokenSequenceVerification");
      duty.setOrganisation(orgRepos.findAll().get(0));
      duty.setSortOrder(dutyRepos.findMaxSortOrder() + 2); // out of sequence
      duty = dutyService.saveOrUpdateDuty(duty);

      assertFalse("Sort order sequence verification didn't detect out-of-sequence sort orders!", 
            areSortOrdersInSequence(dutyRepos.findByActiveTrue()));
   }

   @Test
   public void testSortOrderAfterSoftDeleteOfFirstSortOrder(){
      Duty duty = new Duty();
      duty.setName("delete me please");
      duty.setOrganisation(orgRepos.findAll().get(0));
      duty.setSortOrder(1);

      duty = dutyService.saveOrUpdateDuty(duty);

      duty.setActive(false);
      duty = dutyService.saveOrUpdateDuty(duty);

      assertTrue("Sort orders not sequencing correctly after soft delete!", areSortOrdersInSequence(dutyRepos.findByActiveTrue()));
   }

   @Test
   public void testSortOrderAfterSoftDeleteOfLastSortOrder(){
      Duty duty = new Duty();
      duty.setName("delete me please");
      duty.setOrganisation(orgRepos.findAll().get(0));
      duty.setSortOrder(dutyRepos.findMaxSortOrder() + 1);

      duty = dutyService.saveOrUpdateDuty(duty);

      duty.setActive(false);
      duty = dutyService.saveOrUpdateDuty(duty);

      assertTrue("Sort orders not sequencing correctly after soft delete!", areSortOrdersInSequence(dutyRepos.findByActiveTrue()));
   }

   private boolean areSortOrdersInSequence(List<Duty> duties){
      Set<Integer> sortOrders = new HashSet<Integer>();

      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;

      for(Duty d : duties){
         final int sortOrder = d.getSortOrder();

         if(sortOrders.contains(sortOrder)){
            return false;
         }

         sortOrders.add(sortOrder);

         if(sortOrder < min){
            min = sortOrder;
         }

         if(sortOrder > max){
            max = sortOrder;
         }
      }

      return duties.isEmpty() || (min == 1 && max == sortOrders.size());
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
