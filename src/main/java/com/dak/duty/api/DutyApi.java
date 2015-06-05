package com.dak.duty.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.api.util.SortOrder;
import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.service.DutyService;

@Controller
@RequestMapping("/api/duty")
public class DutyApi {
   
   private static final Logger logger = LoggerFactory.getLogger(DutyApi.class);
   
   @Autowired
   DutyRepository dutyRepos;
   
   @Autowired
   DutyService dutyService;
   
   @RequestMapping(method = RequestMethod.DELETE)
   public @ResponseBody JsonResponse delete(@RequestBody Duty duty){
      logger.debug("duty.delete({})", duty);
      
      duty = dutyRepos.findOne(duty.getId());
      duty.setActive(false);
      duty.setSortOrder(1);
      duty = dutyService.saveOrUpdateDuty(duty);
      
      return new JsonResponse(ResponseStatus.OK, "Duty " + duty.getId() + " deleted");
   }
   
   @RequestMapping(value="/{id}", method = RequestMethod.GET)
   public @ResponseBody Duty get(@PathVariable("id") Long id){
      logger.debug("duty.get({})", id);
      
      return dutyRepos.findOne(id);
   }
   
   @RequestMapping(method = RequestMethod.POST)
   public @ResponseBody JsonResponse save(@RequestBody Duty duty){
      logger.debug("duty.save({})", duty);
      duty = dutyService.saveOrUpdateDuty(duty);
      
      return new JsonResponse(ResponseStatus.OK, "Duty saved with id " + duty.getId());
   }
   
   @RequestMapping(value = "/sortOrder", method = RequestMethod.POST)
   public @ResponseBody JsonResponse saveSortOrder(@RequestBody List<SortOrder> params){
      logger.debug("duty.saveSortOrder({})", params);
      
      final HashMap<Long, Integer> sortOrderSet = SortOrder.getSortMap(params);
      final List<Duty> allActiveDuties = dutyRepos.findByActiveTrue();
      final List<Duty> dutiesToUpdate = new ArrayList<Duty>();
      
      for(Duty duty : allActiveDuties){
         if(sortOrderSet.containsKey(duty.getId())){
            final int newSortOrder = sortOrderSet.get(duty.getId());
            final int oldSortOrder = duty.getSortOrder();
            
            if(newSortOrder != oldSortOrder){
               duty.setSortOrder(newSortOrder);
               dutiesToUpdate.add(duty);
            }
         } else {
            return new JsonResponse(ResponseStatus.ERROR, "Duty " + duty.getId() + " was not included in sort. Action cancelled.");
         }
      }
      
      if(dutiesToUpdate.size() > 0){
         dutyRepos.save(dutiesToUpdate);
      }
      
      return new JsonResponse(ResponseStatus.OK, "Sort orders updated");
   }
   
   @RequestMapping(value = "/sortOrder", method = RequestMethod.GET)
   public @ResponseBody List<SortOrder> getSortOrder(){
      logger.debug("duty.getSortOrder({})");
      
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
   
}
