package com.dak.duty.service.container;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.dak.duty.model.Event;
import com.dak.duty.model.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class EventCalendarNode {
	@Getter
	private final long id;

	@Getter
	private final String title;

	@Getter
	@JsonProperty("start")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm'Z'")
	private final LocalDateTime eventDate;

	@Getter
	@JsonProperty("end")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm'Z'")
	private final LocalDateTime endDate;

	public EventCalendarNode(final long id, final String title, final LocalDate eventDate, final EventType eventType) {
		this.id = id;
		this.title = title;
		
		

		if (eventType != null && eventType.getStartTime() != null) {
			this.eventDate = EventCalendarNode.addTime(eventDate, eventType.getStartTime());

			if (eventType.getEndTime() != null) {
				this.endDate = EventCalendarNode.addTime(eventDate, eventType.getEndTime());
			} else {
				this.endDate = null;
			}
		} else {
			this.eventDate = eventDate.atStartOfDay();
			this.endDate = null;
		}

	}
	
	public static EventCalendarNode fromEvent(final Event e) {
		return new EventCalendarNode(e.getId(), e.getEventType().getName(), e.getDateEvent(), e.getEventType());
	}

	private static LocalDateTime addTime(final LocalDate eventDate, final LocalTime time) {
		return LocalDateTime.of(eventDate, time);
	}
}
