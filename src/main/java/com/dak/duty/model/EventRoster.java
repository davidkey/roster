package com.dak.duty.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@ToString
public class EventRoster {

	@Getter
	private Event event;

	@Getter
	private List<Map.Entry<Duty, Person>> dutiesAndPeople;

	public EventRoster(final Event event) {
		this.init(event);
	}

	public EventRoster(final Event event, final Set<EventRosterItem> eventRosterItems) {
		this.init(event);

		final Set<EventRosterItem> myEventRosterItems = new HashSet<>(eventRosterItems);

		for (final Entry<Duty, Person> pair : this.dutiesAndPeople) {
			for (final EventRosterItem eri : myEventRosterItems) {
				if (eri.getDuty().getId() == pair.getKey().getId()) {
					pair.setValue(eri.getPerson());
					myEventRosterItems.remove(eri);
					break;
				}
			}

		}
	}

	private void init(final Event event) {
		this.event = event;

		final List<Duty> dutiesForEvent = event.getEventType().getDuties();
		this.dutiesAndPeople = new java.util.ArrayList<>();

		for (final Duty d : dutiesForEvent) {
			final java.util.Map.Entry<Duty, Person> pair1 = new java.util.AbstractMap.SimpleEntry<>(d, null);
			this.dutiesAndPeople.add(pair1);
		}
	}
}
