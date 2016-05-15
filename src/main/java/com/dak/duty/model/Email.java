package com.dak.duty.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {
	private String from;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private String subject;
	private String message;

	public Email() {
	}

	public Email(final String from, final String to, final String subject, final String message) {
		this.from = from;

		this.to = new ArrayList<>(1);
		this.to.add(to);
		this.subject = subject;
		this.message = message;
	}

	public Email(final String from, final List<String> to, final List<String> cc, final List<String> bcc) {
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
	}

	public Email(final String from, final List<String> to, final List<String> cc, final List<String> bcc, final String subject,
			final String message) {
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.message = message;
	}

}
