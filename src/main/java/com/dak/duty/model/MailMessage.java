package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "mail_msg")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MailMessage implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "mail_id_seq", sequenceName = "mail_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "mail_id_seq")
   @Column(nullable = false)
   private long id;

   private String recipient;
   private String sender;

   @Column(name = "fromAddress")
   private String from;
   private String subject;
   private String bodyPlain; 
   private String strippedText; 
   private String strippedSignature;
   private String bodyHtml;
   private String strippedHtml; 
   private int attachmentCount; 
   private String attachementX; 
   private int timestamp;
   private String token;
   private String signature;
   private String messageHeaders; 
   private String contentIdMap; 

}
