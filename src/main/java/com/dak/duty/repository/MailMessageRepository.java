package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.MailgunMailMessage;

@Repository
public interface MailMessageRepository extends JpaRepository<MailgunMailMessage, Long>{
   List<MailgunMailMessage> findAllByActiveTrue(Sort sort);
   List<MailgunMailMessage> findAllBySignature(String signature);
}
