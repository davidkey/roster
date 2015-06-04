package com.dak.duty.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;

@Service
@Transactional
public class DutyService {

   @Autowired
   DutyRepository dutyRepos;

   public Duty saveOrUpdateDuty(Duty duty){

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
}
