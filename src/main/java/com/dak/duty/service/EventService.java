package com.dak.duty.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.dak.duty.exception.InvalidIdException;
import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.model.comparable.EventRosterItemSortByDutyOrder;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.IntervalService.EventTypeDetailNode;
import com.dak.duty.service.container.EventCalendarNode;
import com.dak.duty.service.container.comparable.EventCalendarNodeSortByDate;

import lombok.NonNull;

@Service
@Transactional
public class EventService {

	private static final Logger logger = LoggerFactory.getLogger(EventService.class);

	@Autowired
	PersonRepository personRepos;

	@Autowired
	PersonService personService;

	@Autowired
	EventRepository eventRepos;

	@Autowired
	EventTypeRepository eventTypeRepos;

	@Autowired
	IntervalService intervalService;

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	IAuthenticationFacade authenticationFacade;

	/**
	 * Attempt to fill any empty slots in any current and future events.
	 * @return number of slots filled
	 */
	public int fillEmptySlots() {

		int slotsFilled = 0;
		final List<Event> allCurrentAndFutureEvents = this.eventRepos
				.findAllByDateEventGreaterThanEqual(this.intervalService.getCurrentSystemDate());

		for (final Event event : allCurrentAndFutureEvents) {
			if (!event.isRosterFullyPopulated()) {
				slotsFilled += this.fillEmptySlots(event);
			}
		}

		return slotsFilled;
	}

	/**
	 * Attempts to fill any empty slots in Event.
	 * @param event
	 * @return number of slots filled
	 */
	public int fillEmptySlots(final Event event) {
		return this.fillEmptySlots(event, new HashSet<Person>());
	}

	/**
	 * Attempts to fill any empty slots in Event, exluding Person.
	 * @param event
	 * @param person : person to exclude from duty
	 * @return number of slots filled
	 */
	public int fillEmptySlots(final Event event, final Person person) {
		final Set<Person> singlePersonSet = new HashSet<>(1);
		singlePersonSet.add(person);

		return this.fillEmptySlots(event, singlePersonSet);
	}

	/**
	 * Attempts to fill any empty slots in Event, excluding certain people
	 * @param event : Event to fill
	 * @param peopleExcluded : Set of People to exclude from duty
	 * @return number of slots filled
	 */
	public int fillEmptySlots(@NonNull final Event event, @NonNull final Set<Person> peopleExcluded) { // boolean ??
		if (event.isRosterFullyPopulated()) {
			return 0; // nothing to fill
		}

		final EventRoster currentEventRoster = new EventRoster(event, event.getRoster()); // need to populate (as much as
																														// we
		// can...) from event.getRoster()

		int slotsFilled = 0;
		for (int i = 0; i < currentEventRoster.getDutiesAndPeople().size(); i++) {
			final Person currentPerson = currentEventRoster.getDutiesAndPeople().get(i).getValue();

			if (currentPerson == null) { // empty slot
				final Duty currentDuty = currentEventRoster.getDutiesAndPeople().get(i).getKey();
				final Person personForDuty = this.personService.getPersonForDuty(currentDuty, currentEventRoster, peopleExcluded);

				if (personForDuty != null) { // if we found a candidate, add them to Event Roster
					final EventRosterItem eri = new EventRosterItem();
					eri.setDuty(currentDuty);
					eri.setPerson(personForDuty);
					eri.setEvent(event);

					event.addEventRosterItem(eri);
					slotsFilled++;
				}
			}
		}

		if (slotsFilled > 0) {
			event.setApproved(false); // un-approve !!
			this.eventRepos.save(event);
		}

		return slotsFilled;
	}

	public EventType saveEventType(final EventType eventType) {

		if (eventType.getOrganisation() != null
				&& !eventType.getOrganisation().getId().equals(this.authenticationFacade.getOrganisation().getId())) {
			throw new RosterSecurityException("can't do that");
		} else {
			eventType.setOrganisation(this.authenticationFacade.getOrganisation());
		}

		if (EventTypeInterval.DAILY.equals(eventType.getInterval())) {
			eventType.setIntervalDetail(null); // clear out interval detail if this is now a daily event type
		}

		/**
		 * couldn't figure out how to cleanly handle checkboxes for duties, so manually marshalling Duty objects
		 */
		final List<Duty> duties = eventType.getDuties();
		final List<Duty> fixedDuties = new ArrayList<>();
		if (duties != null) {
			for (int i = 0, len = duties.size(); i < len; i++) {
				final long dutyId = duties.get(i).getId();
				if (dutyId > 0) {
					final Duty duty = this.dutyRepos.findOne(dutyId);

					if (duty == null) {
						throw new InvalidIdException("Invalid duty id " + dutyId);
					}

					fixedDuties.add(duty);
				}
			}

			eventType.getDuties().clear();
			eventType.setDuties(fixedDuties);
		}

		return this.eventTypeRepos.save(eventType);
	}

	public boolean optPersonAndDutyOutOfEvent(@NonNull final Person person, @NonNull final Duty duty, @NonNull final Event event) {
		for (final EventRosterItem eri : event.getRoster()) {
			if (eri.getDuty().getId() == duty.getId() && eri.getPerson().getId() == person.getId()) {
				event.getRoster().remove(eri);
				this.eventRepos.save(event);

				// try to repopulate hole in roster
				// SHOULD THIS HAPPEN AUTOMATICALLY OR BE A MANUAL STEP? Maybe this should be a setting somewhere ...
				final int slotsFilled = this.fillEmptySlots(event, person);
				EventService.logger.debug("optPersonAndDutyOutOfEvent() - was slot replaced successfully ? Slots filled: {}", slotsFilled);

				return true;
			}
		}

		return false;
	}

	public List<EventCalendarNode> getAllFutureEventCalendarNodes(final Date startDate) {
		final List<EventCalendarNode> nodes = new ArrayList<>();

		final List<Event> events = this.eventRepos.findAllByDateEventGreaterThanEqual(startDate);
		for (final Event e : events) {
			nodes.add(new EventCalendarNode(e.getId(), e.getEventType().getName(), e.getDateEvent(), e.getEventType()));
		}

		Collections.sort(nodes, new EventCalendarNodeSortByDate());
		return nodes;
	}

	public List<EventCalendarNode> getEventCalendarNodesForMonth(final Date monthDate) {
		final List<EventCalendarNode> nodes = new ArrayList<>();

		final Date startDate = this.intervalService.getFirstDayOfMonth(monthDate);
		final Date endDate = this.intervalService.getLastDayOfMonth(startDate);

		final List<Event> events = this.eventRepos.findEventsByDateBetween(startDate, endDate);
		for (final Event e : events) {
			nodes.add(new EventCalendarNode(e.getId(), e.getEventType().getName(), e.getDateEvent(), e.getEventType()));
		}

		Collections.sort(nodes, new EventCalendarNodeSortByDate());
		return nodes;
	}

	public int approveAllRosters() {
		return this.eventRepos.setApprovedStatusOnAllEvents(true);
	}

	public int unApproveAllRosters() {
		return this.eventRepos.setApprovedStatusOnAllEvents(false);
	}

	public int createAndSaveMissingEvents() {
		final Date maxEventDate = this.eventRepos.findMaxEventDate();

		if (maxEventDate == null) {
			return 0; // there's never even been a normal event generation process, so don't bother trying to create
							// missing events
		}

		final List<Event> missingEvents = this.getMissingEventsForRange(this.intervalService.getCurrentSystemDate(), maxEventDate);

		for (final Event e : missingEvents) {
			final EventRoster er = this.getRosterForEvent(e);
			e.setEventRoster(er);
			this.updatePreferenceRankingsBasedOnRoster(er);
		}

		this.eventRepos.save(missingEvents);

		return missingEvents.size();
	}

	/**
	 * Get missing events for date range.
	 * @param startDate (inclusive)
	 * @param endDate (inclusive)
	 * @return
	 */
	protected List<Event> getMissingEventsForRange(final Date startDate, final Date endDate) {
		final List<Event> missingEvents = new ArrayList<>();
		final List<EventType> eventTypesWithNoEvents = this.eventTypeRepos.getEventTypesWithNoEvents();

		for (final EventType et : eventTypesWithNoEvents) {
			Date currDate = this.intervalService.getFirstDayOfMonth(startDate);

			while (currDate.compareTo(endDate) <= 0) {
				final List<Date> eventDays = this.intervalService.getDaysOfMonthForEventType(currDate, et);
				for (final Date day : eventDays) {
					if (day.compareTo(startDate) >= 0 /* && day.compareTo(endDate) <= 0 */) { // commented out because we
																														// want to generate possible
																														// events through EOM
						final Event event = new Event();
						event.setEventType(et);
						event.setDateEvent(day);
						event.setApproved(false);
						event.setName(et.getName());

						missingEvents.add(event);
					}
				}

				currDate = this.intervalService.getFirstDayOfNextMonth(currDate);
			}
		}

		return missingEvents;
	}

	public int createAndSaveEventsForNextMonth() {
		final Date mostRecentGenerationDate = this.eventRepos.findMaxEventDate();
		Date startDate = null;
		if (mostRecentGenerationDate == null) {
			startDate = this.intervalService.getFirstDayOfMonth(this.intervalService.getCurrentSystemDate());
		} else {
			startDate = this.intervalService.getFirstDayOfNextMonth(mostRecentGenerationDate);
		}

		return this.createAndSaveEventsForMonth(startDate);
	}

	public int createAndSaveEventsForMonth(final Date startDate) {

		final Map<EventTypeDetailNode, List<Date>> eventTypeDays = new HashMap<>();

		for (final EventTypeInterval eti : EventTypeInterval.values()) {
			final List<EventType> eventTypes = this.eventTypeRepos.findByInterval(eti);
			for (final EventType et : eventTypes) {

				final EventTypeDetailNode etdn = this.intervalService.createEventTypeDetailNode(eti, et.getIntervalDetail());
				if (!eventTypeDays.containsKey(etdn)) {
					final List<Date> results = this.intervalService.getDaysOfMonthForInterval(startDate, eti, et.getIntervalDetail());
					eventTypeDays.put(etdn, results);
				}
			}
		}

		final List<EventType> allEventTypes = this.eventTypeRepos.findAll();
		final List<Event> eventsToAdd = new ArrayList<>();

		for (final EventType et : allEventTypes) {
			final EventTypeDetailNode etdn = this.intervalService.createEventTypeDetailNode(et.getInterval(), et.getIntervalDetail());
			if (eventTypeDays.containsKey(etdn)) {
				final List<Date> daysToProcessForEvent = eventTypeDays.get(etdn);
				for (final Date d : daysToProcessForEvent) {
					final Event e = new Event();
					e.setDateEvent(d);
					e.setEventType(et);
					e.setName(et.getName());
					e.setApproved(false);
					eventsToAdd.add(e);
				}
			}
		}

		for (final Event e : eventsToAdd) {
			final EventRoster er = this.getRosterForEvent(e);
			e.setEventRoster(er);
			this.updatePreferenceRankingsBasedOnRoster(er);
		}

		this.eventRepos.save(eventsToAdd);

		return eventsToAdd.size();
	}

	public void updatePreferenceRankingsBasedOnRoster(final EventRoster eventRoster) {
		// create set of people with duties today
		final Set<Person> peopleWithDuties = new HashSet<>();
		for (int i = 0; i < eventRoster.getDutiesAndPeople().size(); i++) {
			CollectionUtils.addIgnoreNull(peopleWithDuties, eventRoster.getDutiesAndPeople().get(i).getValue());
		}

		// make them less likely to have to do anything next time (reduce their ranking)
		for (final Person p : peopleWithDuties) {

			final Set<PersonDuty> personDuties = p.getDuties();
			for (final PersonDuty pd : personDuties) {
				// did this person do this duty today?
				if (EventService.personDidThisDuty(p, pd.getDuty(), eventRoster)) {
					// make them very unlikely to have to do the same thing again
					pd.setAdjustedPreference(0);
				} else {
					// make them roughly half as likely to have to do anything going forward
					pd.setAdjustedPreference(pd.getAdjustedPreference() / 2);
				}
			}
		}

		this.personRepos.save(peopleWithDuties);

		// increment everyone else's adjusted preference by 1
		List<Person> peopleNotServing = null;
		if (CollectionUtils.isEmpty(peopleWithDuties)) {
			peopleNotServing = this.personRepos.findAll();
		} else {
			// peopleNotServing = personRepos.findByActiveTrueAndIdNotIn(getIds(peopleWithDuties));
			peopleNotServing = this.personRepos.findAll(Specifications.where(PersonSpecs.isActive()).and(PersonSpecs.sameOrg())
					.and(PersonSpecs.idNotIn(EventService.getIds(peopleWithDuties)))); // TO
			// DO
			// --
			// TEST
			// THIS!!!
		}

		if (!CollectionUtils.isEmpty(peopleNotServing)) {
			for (final Person p : peopleNotServing) {
				final Set<PersonDuty> personDuties = p.getDuties();
				for (final PersonDuty pd : personDuties) {
					pd.incrementWeightedPreferenceIfNeeded();
				}
			}
		}

		this.personRepos.save(peopleNotServing);
	}

	public EventRoster getRosterForEvent(final Event event) {

		// populate event roster
		final EventRoster eventRoster = new EventRoster(event);
		for (int i = 0; i < eventRoster.getDutiesAndPeople().size(); i++) {
			final Person personForDuty = this.personService.getPersonForDuty(eventRoster.getDutiesAndPeople().get(i).getKey(), eventRoster);
			eventRoster.getDutiesAndPeople().get(i).setValue(personForDuty);
		}

		// updatePreferenceRankingsBasedOnRoster(eventRoster); // <- don't forget to call this from controller once user
		// has "approved" event roster

		return eventRoster;
	}

	public List<EventRosterItem> getSortedRosterIncludingEmptySlots(@NonNull final Long eventId) {
		return this.getSortedRosterIncludingEmptySlots(this.eventRepos.findOne(eventId));
	}

	public List<EventRosterItem> getSortedRosterIncludingEmptySlots(@NonNull final Event event) {
		final List<Duty> allDutiesForEventType = event.getEventType().getDuties();
		final List<EventRosterItem> sortedRoster = new ArrayList<>(event.getRoster());

		/**
		 * If we just sorted our List<EventRosterItem> and returned it, we would not be including unassigned slots. The
		 * following adds "empty" records as needed to ensure blank slots are displayed in application where needed.
		 */
		if (sortedRoster.size() < allDutiesForEventType.size()) {
			for (final Duty d : allDutiesForEventType) {
				final int expectedDutyCount = this.getNumberOccurencesDuty(allDutiesForEventType, d);
				final int actualDutyCount = this.getNumberOccurencesDuty(this.getDuties(sortedRoster), d);

				if (expectedDutyCount != actualDutyCount) {
					for (int i = actualDutyCount; i < expectedDutyCount; i++) {
						final EventRosterItem eri = new EventRosterItem();
						eri.setDuty(d);
						eri.setEvent(event);
						eri.setPerson(null);
						sortedRoster.add(eri);
					}
				}
			}
		}

		Collections.sort(sortedRoster, new EventRosterItemSortByDutyOrder());

		return sortedRoster;
	}

	private List<Duty> getDuties(final List<EventRosterItem> items) {
		final List<Duty> duties = new ArrayList<>();

		if (items != null) {
			for (final EventRosterItem eri : items) {
				duties.add(eri.getDuty());
			}
		}

		return duties;
	}

	private int getNumberOccurencesDuty(final List<Duty> duties, final Duty duty) {
		int num = 0;

		if (duties != null) {
			for (final Duty d : duties) {
				if (d.getId() == duty.getId()) {
					num++;
				}
			}
		}

		return num;
	}

	private static boolean personDidThisDuty(final Person person, final Duty duty, final EventRoster eventRoster) {

		for (int i = 0; i < eventRoster.getDutiesAndPeople().size(); i++) {
			final Duty d = eventRoster.getDutiesAndPeople().get(i).getKey();
			if (d != null && d.getId() == duty.getId()) {
				final Person dutyDoer = eventRoster.getDutiesAndPeople().get(i).getValue();
				return dutyDoer != null && dutyDoer.getId() == person.getId();
			}
		}

		return false;
	}

	private static Set<Long> getIds(@NonNull final Set<Person> people) {
		final Set<Long> ids = new HashSet<>(people.size());

		for (final Person p : people) {
			ids.add(p.getId());
		}

		return ids;

	}
}
