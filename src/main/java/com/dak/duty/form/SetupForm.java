package com.dak.duty.form;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SetupForm {
   @Email
   @NotEmpty
   private String emailAddress;
   
   @Size(min=2, max=64)
   @NotEmpty
   private String nameFirst;
   
   @Size(min=2, max=64)
   @NotEmpty
   private String nameLast;
   
   @Size(min=6, max=64)
   @NotEmpty
   private String password;
   
   @Size(min=6, max=64)
   @NotEmpty
   private String confirmPassword;
}
