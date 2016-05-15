package com.dak.duty.exception;

public class InvalidIdException extends RuntimeException {
	private static final long serialVersionUID = -3388314822393503485L;

	public InvalidIdException(final String msg) {
		super(msg);
	}
}
