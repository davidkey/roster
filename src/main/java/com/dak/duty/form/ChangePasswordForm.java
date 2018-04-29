package com.dak.duty.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangePasswordForm {
	@NotEmpty
	private String currentPassword;

	@Size(min = 6, max = 64)
	@NotEmpty
	private String newPassword;

	@Size(min = 6, max = 64)
	@NotEmpty
	private String newPasswordConfirm;
}
