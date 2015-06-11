package com.dak.duty.service;

import org.springframework.stereotype.Service;

import com.dak.duty.model.Email;

@Service
public interface EmailService<Msg extends Object> {

   public boolean send(Email email);
   public boolean validateIncoming(Msg msg);
}
