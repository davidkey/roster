package com.dak.duty.model.enums;

public enum IntervalWeekly {
   
   MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
   
   public static boolean isEnumValue(final String input){
      
      if(input == null){
         return false;
      }
      
      try{
         IntervalWeekly.valueOf(IntervalWeekly.class, input);
         return true;
      } catch (IllegalArgumentException iae){
         return false;
      }
     
   }

}
