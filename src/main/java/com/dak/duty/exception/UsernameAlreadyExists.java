package com.dak.duty.exception;

public class UsernameAlreadyExists extends RuntimeException {
	private static final long serialVersionUID = 2833747403790053418L;

	public UsernameAlreadyExists(final String msg) {
		super(msg);
	}
}
