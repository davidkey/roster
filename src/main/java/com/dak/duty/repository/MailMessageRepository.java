package com.dak.duty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.MailMessage;

@Repository
public interface MailMessageRepository extends JpaRepository<MailMessage, Long>{
   
}
