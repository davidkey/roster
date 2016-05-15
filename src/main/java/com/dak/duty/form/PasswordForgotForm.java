package com.dak.duty.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordForgotForm {
	@Email
	@NotEmpty
	private String emailAddress;
}
