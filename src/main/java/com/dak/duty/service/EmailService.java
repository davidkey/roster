package com.dak.duty.service;

import org.springframework.stereotype.Service;

import com.dak.duty.model.Email;
import com.dak.duty.model.interfaces.MailMessageInterface;

@Service
public interface EmailService<Msg extends MailMessageInterface> {

	boolean send(Email email);

	boolean validateIncoming(Msg msg);
}
