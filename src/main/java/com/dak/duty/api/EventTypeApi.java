package com.dak.duty.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.model.EventType;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.service.EventService;

@Controller
@RequestMapping("/api/eventType")
@PreAuthorize("hasRole('ROLE_USER')")
public class EventTypeApi {

   private static final Logger logger = LoggerFactory.getLogger(EventTypeApi.class);
   
   @Autowired
   EventTypeRepository eventTypeRepos;
   
   @Autowired
   EventService eventService;
   
   @PreAuthorize("hasRole('ROLE_ADMIN')")
   @RequestMapping(method = RequestMethod.DELETE)
   public @ResponseBody JsonResponse delete(@RequestBody EventType eventType){
      logger.debug("eventType.delete({})", eventType);
      
      eventType = eventTypeRepos.findOne(eventType.getId());
      eventType.setActive(false);
      eventTypeRepos.save(eventType);
      
      return new JsonResponse(ResponseStatus.OK, "EventType " + eventType.getId() + " deleted");
   }
   
   @RequestMapping(value="/{id}", method = RequestMethod.GET)
   public @ResponseBody EventType get(@PathVariable("id") Long id){
      logger.debug("eventType.get({})", id);
      
      return eventTypeRepos.findOne(id);
   }
   
   @PreAuthorize("hasRole('ROLE_ADMIN')")
   @RequestMapping(method = RequestMethod.POST)
   public @ResponseBody JsonResponse save(@RequestBody EventType eventType){
      logger.debug("eventType.save({})", eventType);
      eventType = eventService.saveEventType(eventType);
      
      return new JsonResponse(ResponseStatus.OK, "Event saved with id " + eventType.getId());
   }
}
