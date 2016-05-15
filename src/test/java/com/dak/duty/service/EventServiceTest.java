package com.dak.duty.service;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.api.util.DutyNode;
import com.dak.duty.model.Duty;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.mocking.WithMockCustomUserAdmin;

@RunWith(SpringJUnit4ClassRunner.class)
@WithMockCustomUserAdmin
public class EventServiceTest extends ServiceTest {

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	EventRepository eventRepos;

	@Autowired
	EventTypeRepository eventTypeRepos;

	@Autowired
	DutyService dutyService;

	@Autowired
	PersonService personService;

	@Autowired
	PersonRepository personRepos;

	@Autowired
	EventService eventService;

	@Test
	public void optingOutShouldNotFailWhenNoAlternatePersonIsAvailable() {

		// create a new duty
		final Duty duty = new Duty();
		duty.setName("Duty that nobody wants to do");
		duty.setSortOrder(this.dutyRepos.findMaxSortOrder() + 1);

		this.dutyService.saveOrUpdateDuty(duty);

		// pick a random person, assign them that duty
		final Person person = this.personRepos.findAll().get(0);
		person.addDutyAndPreference(duty, 5);

		// grab a random event type and add our new duty to it
		// EventType eventType = eventTypeRepos.findAll().get(0);

		final EventType eventType = new EventType();
		eventType.setActive(true);
		eventType.setInterval(EventTypeInterval.DAILY);
		eventType.setOrganisation(person.getOrganisation());
		eventType.setStartTime(new Date());
		eventType.setEndTime(new Date());
		eventType.setName("My New Event That Nobody Likes");

		eventType.addDuty(duty);

		this.eventTypeRepos.save(eventType);
		this.personService.save(person);
		this.eventService.createAndSaveEventsForMonth(new Date());

		final List<DutyNode> duties = this.personService.getUpcomingDuties(person);
		Assert.assertFalse("duties shouldn't be empty here!", duties.isEmpty());

		boolean foundOne = false;
		for (final DutyNode dn : duties) {
			if (dn.getDutyId().equals(duty.getId())) {
				Assert.assertTrue("couldn't opt out of event",
						this.eventService.optPersonAndDutyOutOfEvent(person, duty, this.eventRepos.findOne(dn.getEventId())));
				foundOne = true;
				// break;
			}
		}

		Assert.assertTrue("this person didn't get assigned this duty - that's a problem!", foundOne);
	}

}
