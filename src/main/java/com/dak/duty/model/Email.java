package com.dak.duty.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {
   private String from;
   private List<String> to;
   private List<String> cc;
   private List<String> bcc;
   private String subject;
   private String message;

   public Email() {}
   
   public Email(String from, String to, String subject, String message){
      this.from = from;
      
      this.to = new ArrayList<String>(1);
      this.to.add(to);
      this.subject = subject;
      this.message = message;
   }

   public Email(String from, List<String> to, List<String> cc, List<String> bcc) {
       this.from = from;
       this.to = to;
       this.cc = cc;
       this.bcc = bcc;
   }

   public Email(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String message) {
       this.from = from;
       this.to = to;
       this.cc = cc;
       this.bcc = bcc;
       this.subject = subject;
       this.message = message;
   }

}
