package com.dak.duty.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;

public class EventRoster {

   @Getter
   final private Event event;

   @Getter
   private List<Map.Entry<Duty, Person>> dutiesAndPeople;

   public EventRoster(final Event event){
      this.event = event; 

      final List<Duty> dutiesForEvent = event.getEventType().getDuties();   
      dutiesAndPeople = new java.util.ArrayList<>();

      for(Duty d : dutiesForEvent){
         java.util.Map.Entry<Duty, Person> pair1 = new java.util.AbstractMap.SimpleEntry<>(d, null);
         dutiesAndPeople.add(pair1);
      }
   }
}
