package com.dak.duty.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.exception.SortOrderException;
import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.container.SortOrder;

@Service
@Transactional
public class DutyService {

   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   PersonService personService;
   
   @Autowired
   IAuthenticationFacade authenticationFacade;

   public Duty saveOrUpdateDuty(Duty duty){
      
      if(duty.getOrganisation() == null){
         duty.setOrganisation(authenticationFacade.getOrganisation());
      } else if(!duty.getOrganisation().getId().equals(authenticationFacade.getOrganisation().getId())){
         throw new RosterSecurityException("can't do that");
      }

      /**
       * If this is a soft delete:
       *  1) Update name accordingly, decrement sort order [>=] current duty.sortOrder
       * 
       * Otherwise (update or a new duty)
       *  1) Decrement all sort orders [>] old sort order (if this is an update, not a new item)
       *  2) Increment all sort orders [>=] new / updated duty sort order IF this isn't an inactive duty
       *  3) Persist duty w/ new sort order
       */
      if(duty.getId() > 0){ // if this is an update, not a new entity
         final Duty dutyBeforeChanges = dutyRepos.findOne(duty.getId());

         if(!duty.getActive() && dutyBeforeChanges.getActive()){ // if we're deactivating / soft deleting this duty ...
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

            duty.setName(duty.getName() + " (deleted @ " + sdf.format(new Date()) + ")"); // change name to show soft delete and to prevent key errors if another with same name added later
            dutyRepos.decrementSortOrderAboveAndIncludingExcludingDutyId(dutyBeforeChanges.getSortOrder(), duty.getId());
         }
         if(duty.getActive() && !duty.getSortOrder().equals(dutyBeforeChanges.getSortOrder())){
            dutyRepos.decrementSortOrderAboveExcludingDutyId(dutyBeforeChanges.getSortOrder(), duty.getId());
            dutyRepos.incrementSortOrderAboveAndIncludingExcludingDutyId(duty.getSortOrder(), duty.getId());
         }
      } else {
         dutyRepos.incrementSortOrderAboveAndIncluding(duty.getSortOrder());
      }

      return dutyRepos.save(duty);
   }

   public void updateSortOrder(final List<SortOrder> sortOrders) throws SortOrderException{
      final HashMap<Long, Integer> sortOrderSet = SortOrder.getSortMap(sortOrders);
      final List<Duty> allActiveDuties = dutyRepos.findByActiveTrue();
      final List<Duty> dutiesToUpdate = new ArrayList<Duty>();

      int minSortOrder = Integer.MAX_VALUE;
      int maxSortOrder = Integer.MIN_VALUE;
      
      for(Duty duty : allActiveDuties){
         if(sortOrderSet.containsKey(duty.getId())){
            final int newSortOrder = sortOrderSet.get(duty.getId());
            final int oldSortOrder = duty.getSortOrder();

            if(newSortOrder != oldSortOrder){
               duty.setSortOrder(newSortOrder);
               dutiesToUpdate.add(duty);
            }

            if(newSortOrder < minSortOrder){
               minSortOrder = newSortOrder;
            }

            if(newSortOrder > maxSortOrder){
               maxSortOrder = newSortOrder;
            }
         } else {
            throw new SortOrderException("Duty " + duty.getId() + " was not included in sort.");
         }
      }

      if(minSortOrder != 1 || maxSortOrder != allActiveDuties.size()){
         throw new SortOrderException("Invalid sort order sequence");
      }

      if(dutiesToUpdate.size() > 0){
         dutyRepos.save(dutiesToUpdate);
      }
   }
   
   public List<SortOrder> getSortOrders(){
      final List<SortOrder> sortOrders = new ArrayList<SortOrder>();

      List<Duty> activeDuties = dutyRepos.findByActiveTrue();

      for(Duty d : activeDuties){
         SortOrder so = new SortOrder();
         so.setId(d.getId());
         so.setSortOrder(d.getSortOrder());
         sortOrders.add(so);
      }
      
      return sortOrders;
   }
   
   public Duty softDeleteDuty(Duty duty){
      duty = dutyRepos.findOne(duty.getId());
      duty.setActive(false);
      duty.setSortOrder(1);
      duty = this.saveOrUpdateDuty(duty);
      return duty;
   }
}
