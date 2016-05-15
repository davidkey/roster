package com.dak.duty.exception;

public class MailValidationException extends RuntimeException {
	private static final long serialVersionUID = -338831482239303485L;

	public MailValidationException(final String msg) {
		super(msg);
	}
}
