package com.dak.duty.api;

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
      dutyRepos.save(duty);
      
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
      duty = dutyRepos.save(duty);
      
      return new JsonResponse(ResponseStatus.OK, "Duty saved with id " + duty.getId());
   }
}
