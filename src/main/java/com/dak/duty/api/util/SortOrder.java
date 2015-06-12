package com.dak.duty.api.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@ToString
public class SortOrder implements Serializable{
   private static final long serialVersionUID = 1L;
   
   @JsonProperty("id")
   private Long id;
   
   @JsonProperty("sortOrder")
   private Integer sortOrder;
   
   public static HashMap<Long, Integer> getSortMap(@NonNull final List<SortOrder> sortOrders){
      final HashMap<Long, Integer> sortMap = new HashMap<Long, Integer>(sortOrders.size());
      
      for(SortOrder so : sortOrders){
         sortMap.put(so.getId(), so.getSortOrder());
      }
      
      return sortMap;
   }

   /**
    * No constructor; breaks jackson marshaling!
    * https://stackoverflow.com/questions/22102697/spring-mvc-bad-request-with-requestbody
    */
}