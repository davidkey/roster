package com.dak.duty.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

public class EventRoster {
   
   @Getter
   final private Event event;
   
   @Getter
   private Map<Duty, Person> dutiesAndPeople;
   
   public EventRoster(final Event event){
      this.event = event; 
      
      final Set<Duty> dutiesForEvent = event.getEventType().getDuties();   
      dutiesAndPeople = new HashMap<Duty, Person>();
   
      for(Duty d : dutiesForEvent){
         dutiesAndPeople.put(d, null);
      }
   }
   
}
