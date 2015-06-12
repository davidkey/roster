package com.dak.duty.model;

import java.io.Serializable;
import java.util.Date;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "mail_msg")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MailgunMailMessage implements Serializable {
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
   @JsonIgnore
   private String from;
   
   @Column(columnDefinition="text")
   private String subject;
   
   @Column(columnDefinition="text")
   @JsonProperty("body")
   private String bodyPlain; 
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String strippedText; 
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String strippedSignature;
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String bodyHtml;
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String strippedHtml; 
   
   @JsonIgnore
   private int attachmentCount; 
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String attachementX; 
   
   @JsonIgnore
   private int timestamp;
   
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm aaa")
   private Date timestampDate;
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String token;
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String signature;
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String messageHeaders; 
   
   @Column(columnDefinition="text")
   @JsonIgnore
   private String contentIdMap; 
   
   private Boolean read = false;
   
   private Boolean active = true;

}
