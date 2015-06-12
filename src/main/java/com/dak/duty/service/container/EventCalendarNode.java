package com.dak.duty.service.container;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import lombok.Getter;

import com.dak.duty.model.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventCalendarNode {
   @Getter
   private final long id;

   @Getter
   private final String title;

   @Getter
   @JsonProperty("start")
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm'Z'")
   private final Date eventDate;
   
   @Getter
   @JsonProperty("end")
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm'Z'")
   private final Date endDate;
   
   

   public EventCalendarNode(final long id, final String title, final Date eventDate, final EventType eventType){
      this.id = id;
      this.title = title;
      
      if(eventType != null && eventType.getStartTime() != null){
         this.eventDate = addTime(eventDate, eventType.getStartTime());
         
         if(eventType.getEndTime() != null){
            this.endDate = addTime(eventDate, eventType.getEndTime());
         } else {
            this.endDate = null;
         }
      } else {
         this.eventDate = eventDate;
         this.endDate = null;
      }
      
   }
   
   private static Date addTime(Date eventDate, Date dateWithTime){
      Calendar calTime = Calendar.getInstance();
      calTime.setTime(dateWithTime);
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(eventDate);
      cal.setTimeZone(TimeZone.getTimeZone("GMT"));
      cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
      cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      
      return cal.getTime();
   }
}
