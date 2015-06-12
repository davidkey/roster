package com.dak.duty.service;

import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MediaType;

import lombok.NonNull;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dak.duty.exception.MailValidationException;
import com.dak.duty.model.Email;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.repository.MailMessageRepository;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Component
public class MailgunEmailServiceImpl implements EmailService<MailgunMailMessage> {
   
   private static final Logger logger = LoggerFactory.getLogger(MailgunEmailServiceImpl.class);

   @Value("${email.mailgun.apiKey}")
   private String mailgunApiKey;

   @Value("${email.mailgun.host}")
   private String mailgunHost;

   @Value("${email.mailgun.to}")
   private String testEmailAddress;
   
   @Autowired
   private MailMessageRepository mailMessageRepos;

   @Override
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

   private String encode(@NonNull final String key, @NonNull final String data) {
      try{
         Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
         SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
         sha256_HMAC.init(secret_key);

         return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
      } catch (RuntimeException re) {
         throw re;
      } catch (Exception e){
         throw new MailValidationException("Could not encode!");
      }
   }


   @Override
   public boolean validateIncoming(MailgunMailMessage msg) {
      if(mailgunApiKey != null && mailgunApiKey.length() > 0){
         final String mySig = encode(mailgunApiKey, String.valueOf(msg.getTimestamp()) + msg.getToken());
         if(mySig == null || !mySig.equals(msg.getSignature())){
            logger.debug("email validation failed: signature doesn't match expected value.");
            return false;
         }
         
         final List<MailgunMailMessage> msgsWithSameSignature = mailMessageRepos.findAllBySignature(mySig);
         if(!CollectionUtils.isEmpty(msgsWithSameSignature)){
            logger.debug("email validation failed: email with this signature already received.");
            return false;
         }
      }
      
      return true;
   }
   
}
