package com.dak.duty.service.facade.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dak.duty.service.facade.IPasswordResetTokenFacade;

@Component
public class PasswordResetTokenImpl implements IPasswordResetTokenFacade {

	@Override
	public String getNextPasswordResetToken() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
