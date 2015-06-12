package com.dak.duty.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@ToString
public class EventRoster {

   @Getter
   private Event event;

   @Getter
   private List<Map.Entry<Duty, Person>> dutiesAndPeople;

   public EventRoster(final Event event){
      init(event);
   }
   
   public EventRoster(final Event event, final Set<EventRosterItem> eventRosterItems){
      init(event);
      
      final Set<EventRosterItem> myEventRosterItems = new HashSet<EventRosterItem>(eventRosterItems);

      for(Entry<Duty, Person> pair : dutiesAndPeople){
         for(EventRosterItem eri : myEventRosterItems){
            if(eri.getDuty().getId() == pair.getKey().getId()){
               pair.setValue(eri.getPerson());
               myEventRosterItems.remove(eri);
               break;
            }
         }

      }
   }
   
   private void init(final Event event){
      this.event = event; 

      final List<Duty> dutiesForEvent = event.getEventType().getDuties();   
      dutiesAndPeople = new java.util.ArrayList<>();

      for(Duty d : dutiesForEvent){
         java.util.Map.Entry<Duty, Person> pair1 = new java.util.AbstractMap.SimpleEntry<>(d, null);
         dutiesAndPeople.add(pair1);
      }
   }
}
