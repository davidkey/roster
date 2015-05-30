package com.dak.duty.service.container;

import java.util.Date;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventCalendarNode {
   @Getter
   private final long id;

   @Getter
   private final String title;

   @Getter
   @JsonProperty("start")
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
   public final Date eventDate;

   public EventCalendarNode(final long id, final String title, final Date eventDate){
      this.id = id;
      this.title = title;
      this.eventDate = eventDate;
   }
}
