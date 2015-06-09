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

   @Column(columnDefinition="text")
   private String recipient;
   
   @Column(columnDefinition="text")
   private String sender;

   @Column(name = "fromAddress", columnDefinition="text")
   private String from;
   
   @Column(columnDefinition="text")
   private String subject;
   
   @Column(columnDefinition="text")
   private String bodyPlain; 
   
   @Column(columnDefinition="text")
   private String strippedText; 
   
   @Column(columnDefinition="text")
   private String strippedSignature;
   
   @Column(columnDefinition="text")
   private String bodyHtml;
   
   @Column(columnDefinition="text")
   private String strippedHtml; 
   
   private int attachmentCount; 
   
   @Column(columnDefinition="text")
   private String attachementX; 
   
   private int timestamp;
   
   @Column(columnDefinition="text")
   private String token;
   
   @Column(columnDefinition="text")
   private String signature;
   
   @Column(columnDefinition="text")
   private String messageHeaders; 
   
   @Column(columnDefinition="text")
   private String contentIdMap; 

}
