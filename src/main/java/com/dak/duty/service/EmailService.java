package com.dak.duty.service;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Email;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Service
public class EmailService {

   private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

   @Value("${email.mailgun.apiKey}")
   private String mailgunApiKey;

   @Value("${email.mailgun.host}")
   private String mailgunHost;

   @Value("${email.mailgun.to}")
   private String testEmailAddress;

   public boolean send(Email email) {
      if(mailgunApiKey == null || mailgunHost == null){
         logger.error("no apikey and/or host defined for mailgun");
         return false;
      }

      if(email.getFrom() == null || email.getTo() == null || email.getSubject() == null || email.getMessage() == null){
         logger.error("required email field is missing!");
         return false;
      }

      Client client = Client.create();
      client.addFilter(new HTTPBasicAuthFilter("api", mailgunApiKey));

      WebResource webResource = client.resource("https://api.mailgun.net/v3/" + mailgunHost +  "/messages");

      MultivaluedMapImpl formData = new MultivaluedMapImpl();
      formData.add("from", email.getFrom());

      if(testEmailAddress != null && testEmailAddress.length() > 0){
         formData.add("to", testEmailAddress);
      } else {
         for(String to : email.getTo()){
            formData.add("to", to);
         }
      }

      if(email.getCc() != null){
         for(String cc : email.getCc()){
            formData.add("cc", cc);
         }
      }

      if(email.getBcc() != null){
         for(String bcc : email.getBcc()){
            formData.add("bcc", bcc);
         }
      }

      formData.add("subject", email.getSubject());
      formData.add("html", email.getMessage());

      ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
      String output = clientResponse.getEntity(String.class);

      logger.info("Email sent successfully : " + output);
      return true;

   }
}
