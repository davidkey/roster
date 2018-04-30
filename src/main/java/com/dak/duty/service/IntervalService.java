package com.dak.duty.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dak.duty.model.EventType;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;
import com.dak.duty.model.exception.IntervalValidationException;
import com.dak.duty.model.validation.EventTypeIntervalValidation;

import lombok.NonNull;

@Service
public class IntervalService {

	public List<LocalDate> getDaysOfMonthForEventType(@NonNull final LocalDate monthDate, @NonNull final EventType et) {
		return this.getDaysOfMonthForInterval(monthDate, et.getInterval(), et.getIntervalDetail());
	}

	public List<LocalDate> getDaysOfMonthForInterval(@NonNull final LocalDate monthDate, @NonNull final EventTypeInterval eti,
			final String intervalDetail) {
		final List<LocalDate> dates = new ArrayList<>();
		final LocalDate som = this.getFirstDayOfMonth(this.getFirstDayOfMonth(monthDate));
		final LocalDate eom = this.getLastDayOfMonth(som);

		if (intervalDetail == null && !EventTypeInterval.DAILY.equals(eti)) {
			throw new IntervalValidationException("intervalDetail cannot be null when EventTypeInterval =" + eti.toString());
		}

		if (EventTypeInterval.ONCE.equals(eti)) {
			final LocalDate d = EventTypeIntervalValidation.strToDate(intervalDetail);
			if (d != null) {
				final LocalDate onceDate = d;
				if (IntervalService.dateInRange(onceDate, som, eom)) {
					dates.add(d);
				}
			}
		} else if (EventTypeInterval.DAILY.equals(eti)) {
			for (LocalDate dt = som; dt.compareTo(eom) <= 0; dt = dt.plusDays(1)) {
				dates.add(dt);
			}
		} else if (EventTypeInterval.WEEKLY.equals(eti)) {
			final int dayOfWeek = IntervalWeekly.valueOf(intervalDetail).ordinal() + 1;
			for (LocalDate dt = som; dt.compareTo(eom) <= 0; dt = dt.plusDays(1)) {
				if (dt.getDayOfWeek().getValue() == dayOfWeek) {
					dates.add(dt);
					dt = dt.plusDays(6);
				}
			}
		} else if (EventTypeInterval.MONTHLY.equals(eti)) {
			LocalDate dt = som.plusDays(Integer.parseInt(intervalDetail) - 1);
			if (dt.compareTo(eom) > 0) { // if MONTHLY interval occurs on 29th, 30th or 31st and this month doesn't have
													// that many days ...
				dt = eom; // ... just use EOM
			}
			dates.add(dt);
		}

		return dates;
	}

	public LocalDate getCurrentSystemDate() {
		return LocalDate.now();
	}

	public List<LocalDate> getDaysOfQuarterForInterval(@NonNull final LocalDate firstMonthOfQuarter, @NonNull final EventTypeInterval eti,
			@NonNull final String intervalDetail) {
		final List<LocalDate> dates = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			dates.addAll(this.getDaysOfMonthForInterval(firstMonthOfQuarter.plusMonths(i), eti, intervalDetail));
		}

		return dates;
	}

	protected LocalDateTime getDateTime(final LocalDate d) {
		return d.atStartOfDay();
	}

/*	protected Date sanitizeDate(final Date d) {
		return DateUtils.truncate(d, Calendar.DATE);
	}*/

	public LocalDate getFirstDayOfMonth(final LocalDate dt) {
		return dt.withDayOfMonth(1);
	}

	public LocalDate getFirstDayOfMonth(final LocalDateTime d) {
		return this.getFirstDayOfMonth(d.toLocalDate());
	}

	public LocalDate getLastDayOfMonth(final LocalDate dt) {
		return dt.withDayOfMonth(dt.lengthOfMonth());
	}

	public LocalDate getLastDayOfMonth(final LocalDateTime d) {
		return this.getLastDayOfMonth(d.toLocalDate());
	}

	public LocalDate getFirstDayOfNextMonth(@NonNull final LocalDate inputDate) {
		return getFirstDayOfMonth(inputDate).plusMonths(1);
	}

	protected synchronized int getDayOfWeek(final LocalDate d) {
		return d.getDayOfWeek().getValue();
	}
	
	protected static boolean dateInRange(@NonNull final LocalDate dt, @NonNull final LocalDate start, @NonNull final LocalDate end) {
		return dateInRange(dt.atStartOfDay(), start, end);
	}

	protected static boolean dateInRange(@NonNull final LocalDateTime dt, @NonNull final LocalDate start, @NonNull final LocalDate end) {
		return dt.isEqual(start.atStartOfDay()) || (dt.isAfter(start.atStartOfDay()) && dt.isBefore(end.plusDays(1).atStartOfDay()));
	}

	/**
	 *
	 * @param hour: 24 hour clock hour-of-day
	 * @param minute: 0-59
	 * @return
	 */
	public LocalTime getTimeWithoutDate(final int hour, final int minute) {
		return LocalTime.of(hour, minute);
	}

	public EventTypeDetailNode createEventTypeDetailNode(final EventTypeInterval eti, final String detail) {
		return new EventTypeDetailNode(eti, detail);
	}

	public final class EventTypeDetailNode {
		final EventTypeInterval eventTypeInterval;
		final String intervalDetail;

		public EventTypeDetailNode(final EventTypeInterval eventTypeInterval, final String invervalDetail) {
			this.eventTypeInterval = eventTypeInterval;
			this.intervalDetail = invervalDetail;
		}

		public EventTypeInterval getEventTypeInterval() {
			return this.eventTypeInterval;
		}

		public String getIntervalDetail() {
			return this.intervalDetail;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.getOuterType().hashCode();
			result = prime * result + ((this.eventTypeInterval == null) ? 0 : this.eventTypeInterval.hashCode());
			result = prime * result + ((this.intervalDetail == null) ? 0 : this.intervalDetail.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final EventTypeDetailNode other = (EventTypeDetailNode) obj;
			if (!this.getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.eventTypeInterval != other.eventTypeInterval) {
				return false;
			}
			if (this.intervalDetail == null) {
				if (other.intervalDetail != null) {
					return false;
				}
			} else if (!this.intervalDetail.equals(other.intervalDetail)) {
				return false;
			}
			return true;
		}

		private IntervalService getOuterType() {
			return IntervalService.this;
		}
	}
}
