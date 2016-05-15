package com.dak.duty.model.exception;

public class IntervalValidationException extends RuntimeException {
	private static final long serialVersionUID = 4988613816842165398L;

	public IntervalValidationException(final String msg) {
		super(msg);
	}
}
