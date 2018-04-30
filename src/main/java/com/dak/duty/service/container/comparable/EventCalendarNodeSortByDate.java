package com.dak.duty.service.container.comparable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

import com.dak.duty.service.container.EventCalendarNode;

public class EventCalendarNodeSortByDate implements Comparator<EventCalendarNode>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final EventCalendarNode o1, final EventCalendarNode o2) {
		LocalDateTime date1 = null;
		LocalDateTime date2 = null;

		if (o1 != null) {
			if (o1.getEventDate() != null) {
				date1 = o1.getEventDate();
			}
		}

		if (o2 != null) {
			if (o2.getEventDate() != null) {
				date2 = o2.getEventDate();
			}
		}

		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return 1;
			}
		}

		return date1 == null ? -1 : date1.compareTo(date2);
	}

}
