package com.dak.duty.service;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;
import com.dak.duty.model.exception.IntervalValidationException;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.OrganisationRepository;
import com.dak.duty.security.mocking.WithMockCustomUserAdmin;

@RunWith(SpringJUnit4ClassRunner.class)
@WithMockCustomUserAdmin
public class IntervalServiceTest extends ServiceTest {

	@Autowired
	EventService eventService;

	@Autowired
	EventTypeRepository eventTypeRepos;

	@Autowired
	IntervalService intervalService;

	@Autowired
	OrganisationRepository orgRepos;

	@Test
	public void testSettingIntervalFromWeeklyToDaily() {
		EventType et = new EventType();
		et.setName("test event type");
		et.setDescription(et.getName());
		et.setInterval(EventTypeInterval.WEEKLY);
		et.setIntervalDetail(IntervalWeekly.THURSDAY.toString());
		et.setOrganisation(this.orgRepos.findAll().get(0));

		et = this.eventService.saveEventType(et);

		et.setInterval(EventTypeInterval.DAILY);
		et = this.eventService.saveEventType(et);

		final List<Date> dates = this.intervalService.getDaysOfMonthForInterval(this.intervalService.getCurrentSystemDate(), et.getInterval(),
				et.getIntervalDetail());

		Assert.assertNotNull(dates);
		Assert.assertTrue("dates is empty", dates.size() > 0);
	}

	@Test
	public void testDailyEventCanBeCreatedWithoutIntervalDetail() {
		final EventType et = new EventType();
		et.setName("test event type two");
		et.setDescription(et.getName());
		et.setInterval(EventTypeInterval.DAILY);
		et.setOrganisation(this.orgRepos.findAll().get(0));

		final List<Date> dates = this.intervalService.getDaysOfMonthForInterval(this.intervalService.getCurrentSystemDate(), et.getInterval(),
				et.getIntervalDetail());

		Assert.assertNotNull(dates);
		Assert.assertTrue("dates is empty", dates.size() > 0);
	}

	@Test(expected = IntervalValidationException.class)
	public void testWeeklyEventCannotBeSetWithoutDetail() {

		final EventType et = new EventType();
		et.setName("test event type three");
		et.setDescription(et.getName());
		et.setInterval(EventTypeInterval.WEEKLY);
		et.setOrganisation(this.orgRepos.findAll().get(0));

		this.intervalService.getDaysOfMonthForInterval(this.intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());
	}

	@Test(expected = IntervalValidationException.class)
	public void testOnceEventCannotBeSetWithoutDetail() {

		final EventType et = new EventType();
		et.setName("test event type four");
		et.setDescription(et.getName());
		et.setInterval(EventTypeInterval.ONCE);
		et.setOrganisation(this.orgRepos.findAll().get(0));

		this.intervalService.getDaysOfMonthForInterval(this.intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());
	}

	@Test(expected = IntervalValidationException.class)
	public void testMonthlyEventCannotBeSetWithoutDetail() {

		final EventType et = new EventType();
		et.setName("test event type five");
		et.setDescription(et.getName());
		et.setInterval(EventTypeInterval.MONTHLY);
		et.setOrganisation(this.orgRepos.findAll().get(0));

		this.intervalService.getDaysOfMonthForInterval(this.intervalService.getCurrentSystemDate(), et.getInterval(), et.getIntervalDetail());
	}

}
