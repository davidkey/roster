package com.dak.duty.service;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;
import com.dak.duty.model.exception.IntervalValidationException;
import com.dak.duty.repository.EventTypeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/servlet-context-test.xml"})
public class IntervalServiceTest extends ServiceTest {

   @Autowired
   EventService eventService;

   @Autowired
   EventTypeRepository eventTypeRepos;

   @Autowired
   IntervalService intervalService;

   @Test
   public void testSettingIntervalFromWeeklyToDaily(){
      EventType et = new EventType();
      et.setName("test event type");
      et.setDescription(et.getName());
      et.setInterval(EventTypeInterval.WEEKLY);
      et.setIntervalDetail(IntervalWeekly.THURSDAY.toString());

      et = eventService.saveEventType(et);

      et.setInterval(EventTypeInterval.DAILY);
      et = eventService.saveEventType(et);

      List<Date> dates = intervalService.getDaysOfMonthForInterval(intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());

      assertNotNull(dates);
      assertTrue("dates is empty", dates.size() > 0);
   }

   @Test
   public void testDailyEventCanBeCreatedWithoutIntervalDetail(){
      EventType et = new EventType();
      et.setName("test event type two");
      et.setDescription(et.getName());
      et.setInterval(EventTypeInterval.DAILY);
      et = eventService.saveEventType(et);

      List<Date> dates = intervalService.getDaysOfMonthForInterval(intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());

      assertNotNull(dates);
      assertTrue("dates is empty", dates.size() > 0);
   }
   
   @Test(expected = IntervalValidationException.class)
   public void testWeeklyEventCannotBeSetWithoutDetail(){
      
      EventType et = new EventType();
      et.setName("test event type three");
      et.setDescription(et.getName());
      et.setInterval(EventTypeInterval.WEEKLY);
      et = eventService.saveEventType(et);
      
      intervalService.getDaysOfMonthForInterval(intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());
   }
   
   @Test(expected = IntervalValidationException.class)
   public void testOnceEventCannotBeSetWithoutDetail(){
      
      EventType et = new EventType();
      et.setName("test event type four");
      et.setDescription(et.getName());
      et.setInterval(EventTypeInterval.ONCE);
      et = eventService.saveEventType(et);
      
      intervalService.getDaysOfMonthForInterval(intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());
   }
   
   @Test(expected = IntervalValidationException.class)
   public void testMonthlyEventCannotBeSetWithoutDetail(){
      
      EventType et = new EventType();
      et.setName("test event type five");
      et.setDescription(et.getName());
      et.setInterval(EventTypeInterval.MONTHLY);
      et = eventService.saveEventType(et);
      
      intervalService.getDaysOfMonthForInterval(intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());
   }


}