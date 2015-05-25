package com.dak.duty.model.validation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;

public class EventTypeIntervalValidation {
   public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
   
   public static boolean validate(final EventTypeInterval eti, final String value){
      if(eti == null || value == null){ // could this cause problems depending on hibernate's create order?
         return false;
      }
      
      switch (eti){
         case DAILY:
            return true;
         case WEEKLY: // day of week
            return IntervalWeekly.isEnumValue(value);
         case MONTHLY: // day of month
            int valInt = strToInt(value);
            return valInt >= 1 && valInt <= 31;
         case ONCE: // valid date
            return isValidDate(value);
         default:
            return false;
      }
   }

   
   public static int strToInt(final String input){
      int returnVal = -1;
      
      try{
         returnVal = Integer.valueOf(input);
      } catch (NumberFormatException nfe){
         // do nothing
      }
      
      return returnVal;
   }
   
   public static boolean isValidDate(final String input){
      
      try{
        df.parse(input); 
        return true;
      } catch (ParseException pe){
         // do nothing
      }
      
      return false;
   }
   
}
