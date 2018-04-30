package com.dak.duty.api.util;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class DutyNode {
	private final String eventName;
	private final LocalDate eventDate;
	private final String dutyName;
	private final Long dutyId;
	private final Long eventId;

	public DutyNode(final String eventName, final LocalDate eventDate, final String dutyName, final Long dutyId, final Long eventId) {
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.dutyName = dutyName;
		this.dutyId = dutyId;
		this.eventId = eventId;
	}
}
