package com.dak.duty.model.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class EventTypeIntervalValidation {
	private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private EventTypeIntervalValidation() {
		// private & empty constructor
	}

	public static boolean validate(final EventTypeInterval eti, final String value) {
		if (eti == null || (!EventTypeInterval.DAILY.equals(eti) && value == null)) { // could this cause problems
																												// depending on hibernate's create
																												// order?
			return false;
		}

		switch (eti) {
		case DAILY:
			return true;
		case WEEKLY: // day of week
			return IntervalWeekly.isEnumValue(value);
		case MONTHLY: // day of month
			final int valInt = EventTypeIntervalValidation.strToInt(value);
			return valInt >= 1 && valInt <= 31;
		case ONCE: // valid date
			return EventTypeIntervalValidation.isValidDate(value);
		default:
			return false;
		}
	}

	public static int strToInt(final String input) {
		int returnVal = -1;

		try {
			returnVal = Integer.parseInt(input);
		} catch (final NumberFormatException nfe) {
			// do nothing
		}

		return returnVal;
	}

	public static boolean isValidDate(final String input) {

		try {
			fmt.parse(input);
			return true;
		} catch (final DateTimeParseException iae) {
			// do nothing
		}

		return false;
	}

	public static LocalDate strToDate(final String str) {
		try {
			fmt.parse(str);
		} catch (final DateTimeParseException iae) {
			log.debug("error formatting date {}", str, iae);
		}

		return null;
	}

}
