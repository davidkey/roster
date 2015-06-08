package com.dak.duty.api.util;

import java.util.Date;

import lombok.Getter;

@Getter
public class DutyNode {
   private final String eventName;
   private final Date eventDate;
   private final String dutyName;
   
   public DutyNode(final String eventName, final Date eventDate, final String dutyName){
      this.eventName = eventName;
      this.eventDate = eventDate;
      this.dutyName = dutyName;
   }
}
