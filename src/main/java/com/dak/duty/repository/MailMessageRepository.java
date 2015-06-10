package com.dak.duty.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dak.duty.model.MailMessage;

@Repository
public interface MailMessageRepository extends JpaRepository<MailMessage, Long>{
   List<MailMessage> findAllByActiveTrue(Sort sort);
   List<MailMessage> findAllBySignature(String signature);
}
