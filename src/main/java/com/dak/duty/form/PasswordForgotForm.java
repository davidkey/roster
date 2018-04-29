package com.dak.duty.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

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
