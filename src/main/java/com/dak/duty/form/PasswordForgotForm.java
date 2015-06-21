package com.dak.duty.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class PasswordForgotForm {
   @Email
   @NotEmpty
   private String emailAddress;
}
