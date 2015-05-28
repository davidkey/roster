package com.dak.duty.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.NonNull;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;
import com.dak.duty.model.validation.EventTypeIntervalValidation;

@Service
public class IntervalService {
   
   public List<Date> getDaysOfMonthForEventType(@NonNull final Date monthDate, @NonNull final EventType et){
      return getDaysOfMonthForInterval(monthDate, et.getInterval(), et.getIntervalDetail());
   }
   
   public List<Date> getDaysOfMonthForInterval(@NonNull final Date monthDate, @NonNull final EventTypeInterval eti, @NonNull final String intervalDetail){
      final List<Date> dates = new ArrayList<Date>();
      final DateTime som = getFirstDayOfMonth(getFirstDayOfMonth(getDateTime(sanitizeDate(monthDate))));
      final DateTime eom = getLastDayOfMonth(som);

      if(EventTypeInterval.ONCE.equals(eti)){
         final Date d = EventTypeIntervalValidation.strToDate(intervalDetail);
         if(d != null){
            final DateTime onceDate = getDateTime(d);
            if(dateInRange(onceDate, som, eom)){
               dates.add(onceDate.toDate());
            }
         }
      } else if(EventTypeInterval.DAILY.equals(eti)){
         for(DateTime dt = som; dt.compareTo(eom) <= 0; dt = dt.plusDays(1)){
            dates.add(dt.toDate());
         }
      } else if(EventTypeInterval.WEEKLY.equals(eti)){
         final int dayOfWeek = IntervalWeekly.valueOf(intervalDetail).ordinal() + 1;
         for(DateTime dt = som; dt.compareTo(eom) <= 0; dt = dt.plusDays(1)){
            if(dt.getDayOfWeek() == dayOfWeek){
               dates.add(dt.toDate());
            }
         }
      } else if(EventTypeInterval.MONTHLY.equals(eti)){
         DateTime dt = som.plusDays(Integer.valueOf(intervalDetail)-1);
         if(dt.compareTo(eom) > 0){ // if MONTHLY interval occurs on 29th, 30th or 31st and this month doesn't have that many days ...
            dt = eom;               // ... just use EOM
         }
         dates.add(dt.toDate());
      }

      return dates;
   }
   
   public Date getCurrentSystemDate(){
      return sanitizeDate(new Date());
   }
   
   public List<Date> getDaysOfQuarterForInterval(@NonNull final Date firstMonthOfQuarter, @NonNull final EventTypeInterval eti, @NonNull final String intervalDetail){
      final List<Date> dates = new ArrayList<Date>();
      DateTime fmDt = getDateTime(firstMonthOfQuarter);

      for(int i = 0; i < 3; i++){
         dates.addAll(getDaysOfMonthForInterval(fmDt.plusMonths(i).toDate(), eti, intervalDetail));
      }

      return dates;
   }

   protected DateTime getDateTime(final Date d){
      return new DateTime(d);
   }

   protected Date sanitizeDate(final Date d){
      return DateUtils.truncate(d, Calendar.DATE);
   }

   public DateTime getFirstDayOfMonth(final DateTime dt){
      return dt.dayOfMonth().withMinimumValue();
   }
   
   public Date getFirstDayOfMonth(final Date d){
      return getFirstDayOfMonth(getDateTime(sanitizeDate(d))).toDate();
   }

   public DateTime getLastDayOfMonth(final DateTime dt){
      return dt.dayOfMonth().withMaximumValue();
   }
   
   public Date getLastDayOfMonth(final Date d){
      return getLastDayOfMonth(getDateTime(sanitizeDate(d))).toDate();
   }
   
   public Date getFirstDayOfNextMonth(@NonNull final Date inputDate){
      return getFirstDayOfMonth(getDateTime(sanitizeDate(inputDate)).plusMonths(1)).toDate();
   }

   protected synchronized int getDayOfWeek(final Date d){
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);

      return cal.get(Calendar.DAY_OF_WEEK);
   }

   protected static boolean dateInRange(@NonNull final DateTime dt, @NonNull final DateTime start, @NonNull final DateTime end){
      return dt.compareTo(start) >= 0 && dt.compareTo(end) <= 0;
   }
   
   public EventTypeDetailNode createEventTypeDetailNode(final EventTypeInterval eti, final String detail){
      return new EventTypeDetailNode(eti, detail);
   }
   
   public final class EventTypeDetailNode{
      final EventTypeInterval eventTypeInterval;
      final String intervalDetail;
      
      public EventTypeDetailNode(final EventTypeInterval eventTypeInterval, final String invervalDetail){
         this.eventTypeInterval = eventTypeInterval;
         this.intervalDetail = invervalDetail;
      }
      
      public EventTypeInterval getEventTypeInterval(){
         return this.eventTypeInterval;
      }
      
      public String getIntervalDetail(){
         return this.intervalDetail;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + ((eventTypeInterval == null) ? 0 : eventTypeInterval.hashCode());
         result = prime * result + ((intervalDetail == null) ? 0 : intervalDetail.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         EventTypeDetailNode other = (EventTypeDetailNode) obj;
         if (!getOuterType().equals(other.getOuterType()))
            return false;
         if (eventTypeInterval != other.eventTypeInterval)
            return false;
         if (intervalDetail == null) {
            if (other.intervalDetail != null)
               return false;
         } else if (!intervalDetail.equals(other.intervalDetail))
            return false;
         return true;
      }

      private IntervalService getOuterType() {
         return IntervalService.this;
      }
   }
}
