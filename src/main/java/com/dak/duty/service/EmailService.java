package com.dak.duty.service;

import org.springframework.stereotype.Service;

import com.dak.duty.model.Email;
import com.dak.duty.model.interfaces.MailMessageInterface;

@Service
public interface EmailService<Msg extends MailMessageInterface> {

   public boolean send(Email email);
   public boolean validateIncoming(Msg msg);
}
