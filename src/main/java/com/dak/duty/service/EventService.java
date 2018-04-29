package com.dak.duty.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private PersonRepository personRepos;

	@Autowired
	private PersonService personService;

	@Autowired
	private EventRepository eventRepos;

	@Autowired
	private EventTypeRepository eventTypeRepos;

	@Autowired
	private IntervalService intervalService;

	@Autowired
	private DutyRepository dutyRepos;

	@Autowired
	private IAuthenticationFacade authenticationFacade;

	/**
	 * Attempt to fill any empty slots in any current and future events.
	 * @return number of slots filled
	 */
	public int fillEmptySlots() {

		/*
		  Should we allow the same person to have multiple duties on the same day?
			- generate next set allows it
			- fill empty slots does not
		 */

		int slotsFilled = 0;
		final List<Event> allCurrentAndFutureEvents = this.eventRepos.findAllByDateEventGreaterThanEqual(this.intervalService.getCurrentSystemDate());

		for (final Event event : allCurrentAndFutureEvents) {
			if (!event.isRosterFullyPopulated()) {
				slotsFilled += this.fillEmptySlots(event);
				//slotsFilled += this.fillEmptySlots(event, event.getRoster().stream().map(EventRosterItem::getPerson).collect(Collectors.toSet()));
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
		return this.fillEmptySlots(event, Collections.emptySet());
	}

	/**
	 * Attempts to fill any empty slots in Event, exluding Person.
	 * @param event
	 * @param person : person to exclude from duty
	 * @return number of slots filled
	 */
	public int fillEmptySlots(final Event event, final Person person) {
		return this.fillEmptySlots(event, Collections.singleton(person));
	}

	/**
	 * Attempts to fill any empty slots in Event, excluding certain people
	 * @param event : Event to fill
	 * @param peopleExcluded : Set of People to exclude from duty
	 * @return number of slots filled
	 */
	public int fillEmptySlots(@NonNull final Event event, @NonNull final Set<Person> peopleExcluded) {
		if (event.isRosterFullyPopulated()) {
			return 0; // nothing to fill
		}

		final EventRoster currentEventRoster = new EventRoster(event, event.getRoster()); // need to populate (as much as we can...) from event.getRoster()

		final int slotsFilled = fillEmptySlotsRecursive(currentEventRoster, peopleExcluded);

		if (slotsFilled > 0) {
			event.setApproved(false); // un-approve !!
			this.eventRepos.save(event);
		}

		return slotsFilled;
	}

	private int fillEmptySlotsRecursive(@NonNull final EventRoster currentEventRoster, @NonNull final Set<Person> peopleExcluded) {
		if (currentEventRoster.getEvent().isRosterFullyPopulated()) {
			return 0; // nothing to fill
		}

		for(final Entry<Duty, Person> dutyAndPerson : currentEventRoster.getDutiesAndPeople()) {
			final Person currentPerson = dutyAndPerson.getValue();

			if (currentPerson == null) { // empty slot
				final Duty currentDuty = dutyAndPerson.getKey();
				final Optional<Person> personForDuty = this.personService.getPersonForDuty(currentDuty, currentEventRoster, peopleExcluded);

				if (personForDuty.isPresent()) { // if we found a candidate, add them to Event Roster
					final EventRosterItem eri = new EventRosterItem();
					eri.setDuty(currentDuty);
					eri.setPerson(personForDuty.get());
					eri.setEvent(currentEventRoster.getEvent());

					currentEventRoster.getEvent().addEventRosterItem(eri);

					return 1 + fillEmptySlotsRecursive(new EventRoster(currentEventRoster.getEvent(), currentEventRoster.getEvent().getRoster()), peopleExcluded);
				}
			}
		}

		return 0;
	}

	public EventType saveEventType(final EventType eventType) {

		if (eventType.getOrganisation() != null && !eventType.getOrganisation().getId().equals(this.authenticationFacade.getOrganisation().getId())) {
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
				// TODO: SHOULD THIS HAPPEN AUTOMATICALLY OR BE A MANUAL STEP? Maybe this should be a setting somewhere ...
				final int slotsFilled = this.fillEmptySlots(event, person);
				logger.debug("optPersonAndDutyOutOfEvent() - was slot replaced successfully ? Slots filled: {}", slotsFilled);

				return true;
			}
		}

		return false;
	}

	public List<EventCalendarNode> getAllFutureEventCalendarNodes(final Date startDate) {
		return this.eventRepos.findAllByDateEventGreaterThanEqual(startDate).stream()
				.map(EventCalendarNode::fromEvent)
				.sorted(new EventCalendarNodeSortByDate())
				.collect(Collectors.toList());
	}

	public List<EventCalendarNode> getEventCalendarNodesForMonth(final Date monthDate) {
		final Date startDate = this.intervalService.getFirstDayOfMonth(monthDate);
		final Date endDate = this.intervalService.getLastDayOfMonth(startDate);

		return this.eventRepos.findEventsByDateBetween(startDate, endDate).stream()
				.map(EventCalendarNode::fromEvent)
				.sorted(new EventCalendarNodeSortByDate())
				.collect(Collectors.toList());
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

		this.eventRepos.saveAll(missingEvents);

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
					if (day.compareTo(startDate) >= 0 /* && day.compareTo(endDate) <= 0 */) { /* commented out because we  want to generate possible events through EOM */
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

		this.eventRepos.saveAll(eventsToAdd);

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
				if (this.personDidThisDuty(p, pd.getDuty(), eventRoster)) {
					// make them very unlikely to have to do the same thing again
					pd.setAdjustedPreference(0);
				} else {
					// make them roughly half as likely to have to do anything going forward
					pd.setAdjustedPreference(pd.getAdjustedPreference() / 2);
				}
			}
		}

		this.personRepos.saveAll(peopleWithDuties);

		// increment everyone else's adjusted preference by 1
		List<Person> peopleNotServing = null;
		if (CollectionUtils.isEmpty(peopleWithDuties)) {
			peopleNotServing = this.personRepos.findAll();
		} else {
			// peopleNotServing = personRepos.findByActiveTrueAndIdNotIn(getIds(peopleWithDuties));
			
			peopleNotServing = this.personRepos.findAll(PersonSpecs.isActive().and(PersonSpecs.sameOrg()).and(PersonSpecs.idNotIn(getIds(peopleWithDuties))));
			
/*			peopleNotServing = this.personRepos.findAll(
					Specifications.where(PersonSpecs.isActive())
					.and(PersonSpecs.sameOrg())
					.and(PersonSpecs.idNotIn(EventService.getIds(peopleWithDuties))));  TODO TEST THIS!!! */
		}

		if (!CollectionUtils.isEmpty(peopleNotServing)) {

			peopleNotServing.stream()
			.map(Person::getDuties)
			.flatMap(Collection::stream)
			.forEach(PersonDuty::incrementWeightedPreferenceIfNeeded);
		}

		this.personRepos.saveAll(peopleNotServing);
	}

	public EventRoster getRosterForEvent(final Event event) {
		// populate event roster
		final EventRoster eventRoster = new EventRoster(event);

		for(Entry<Duty, Person> dutyAndPerson : eventRoster.getDutiesAndPeople()) {
			final Optional<Person> personForDuty = this.personService.getPersonForDuty(dutyAndPerson.getKey(), eventRoster);

			if(personForDuty.isPresent()) {
				dutyAndPerson.setValue(personForDuty.get());
			}
		}

		// updatePreferenceRankingsBasedOnRoster(eventRoster); // <- don't forget to call this from controller once user has "approved" event roster

		return eventRoster;
	}

	public List<EventRosterItem> getSortedRosterIncludingEmptySlots(@NonNull final Long eventId) {
		Optional<Event> event = this.eventRepos.findById(eventId);

		if(event.isPresent()) {
			return this.getSortedRosterIncludingEmptySlots(event.get());
		} else {
			return Collections.emptyList();
		}
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
		if(items == null) {
			return Collections.emptyList();
		}

		return items.stream().map(EventRosterItem::getDuty).collect(Collectors.toList());
	}

	private int getNumberOccurencesDuty(final List<Duty> duties, final Duty duty) {
		if(duties == null || duties.isEmpty()) {
			return 0;
		}

		return (int) duties.stream().filter(d -> d.getId() == duty.getId()).count();
	}

	private boolean personDidThisDuty(final Person person, final Duty duty, final EventRoster eventRoster) {

		for(Entry<Duty, Person> dutyAndPerson : eventRoster.getDutiesAndPeople()) {
			final Duty d = dutyAndPerson.getKey();
			if (d != null && d.getId() == duty.getId()) {
				final Person dutyDoer = dutyAndPerson.getValue();
				return dutyDoer != null && dutyDoer.getId() == person.getId();
			}
		}

		return false;
	}

	private Set<Long> getIds(@NonNull final Set<Person> people) {
		return people.stream().map(Person::getId).collect(Collectors.toSet());
	}
}
