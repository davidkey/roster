package com.dak.duty.model.validation;

import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;

public class EventTypeIntervalValidation {
   public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
  
   
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
         returnVal = Integer.parseInt(input);
      } catch (NumberFormatException nfe){
         // do nothing
      }
      
      return returnVal;
   }
   
   public static boolean isValidDate(final String input){
      
      try{
        fmt.parseDateTime(input);
        return true;
      } catch (IllegalArgumentException iae){
         // do nothing
      }
      
      return false;
   }
   
   public static Date strToDate(final String str){
      try{
         return fmt.parseDateTime(str).toDate();
       } catch (IllegalArgumentException iae){
          // do nothing
       }

      return null;
   }
   
}
