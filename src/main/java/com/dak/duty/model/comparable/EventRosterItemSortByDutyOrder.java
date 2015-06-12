package com.dak.duty.model.comparable;

import java.io.Serializable;
import java.util.Comparator;

import com.dak.duty.model.EventRosterItem;

public class EventRosterItemSortByDutyOrder implements Comparator<EventRosterItem>, Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   public int compare(EventRosterItem o1, EventRosterItem o2) {
      Integer sortOrder1 = 0;
      Integer sortOrder2 = 0;

      if(o1 != null){
         if(o1.getDuty() != null){
            int tmp = o1.getDuty().getSortOrder();
            sortOrder1 = tmp;
         }
      }

      if(o2 != null){
         if(o2.getDuty() != null){
            int tmp = o2.getDuty().getSortOrder();
            sortOrder2 = tmp;
         }
      }

      return sortOrder1.compareTo(sortOrder2);
   }

}
