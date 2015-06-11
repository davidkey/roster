package com.dak.duty.controller.admin;

import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;

import lombok.NonNull;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.dak.duty.exception.MailValidationException;
import com.dak.duty.model.MailMessage;
import com.dak.duty.repository.MailMessageRepository;

@Controller
@RequestMapping("/mail")
public class MailMessageController {

   @Autowired
   MailMessageRepository mailMessageRepos;

   @Value("${email.mailgun.apiKey}")
   private String mailgunApiKey;

   private static final Logger logger = LoggerFactory.getLogger(MailMessageController.class);

   @RequestMapping(method = RequestMethod.POST)
   public @ResponseBody Boolean postMessage(@ModelAttribute @Valid MailMessage mailMessage){
      logger.debug("postMessage({})", mailMessage);

      if(mailgunApiKey != null && mailgunApiKey.length() > 0){
         final String mySig = encode(mailgunApiKey, String.valueOf(mailMessage.getTimestamp()) + mailMessage.getToken());
         if(mySig == null || !mySig.equals(mailMessage.getSignature())){
            throw new MailValidationException("Signatures don't match!");
         }
         
         final List<MailMessage> msgsWithSameSignature = mailMessageRepos.findAllBySignature(mySig);
         if(!CollectionUtils.isEmpty(msgsWithSameSignature)){
            throw new MailValidationException("Mail message with this signature already received!");
         }
      }

      mailMessageRepos.save(mailMessage);
      return true;
   }

   @InitBinder(value="mailMessage")
   private void bind(WebDataBinder dataBinder, WebRequest webRequest, 
         @RequestParam(value="body-plain", required=false) String bodyPlain,
         @RequestParam(value="stripped-text", required=false) String strippedText,
         @RequestParam(value="stripped-signature", required=false) String strippedSignature,
         @RequestParam(value="body-html", required=false) String bodyHtml,
         @RequestParam(value="stripped-html", required=false) String strippedHtml,
         @RequestParam(value="attachment-count", required=false) Integer attachmentCount,
         @RequestParam(value="attachment-x", required=false) String attachementX,
         @RequestParam(value="message-headers", required=false) String messageHeaders,
         @RequestParam(value="content-id-map", required=false) String contentIdMap,
         @RequestParam(value="timestamp", required=false) int timestamp) {

      MailMessage mailMessage = (MailMessage) dataBinder.getTarget();

      mailMessage.setBodyPlain(bodyPlain);
      mailMessage.setStrippedText(strippedText);
      mailMessage.setStrippedSignature(strippedSignature);
      mailMessage.setBodyHtml(bodyHtml);
      mailMessage.setStrippedHtml(strippedHtml);
      mailMessage.setAttachmentCount(attachmentCount == null ? 0 : attachmentCount);
      mailMessage.setAttachementX(attachementX);
      mailMessage.setMessageHeaders(messageHeaders);
      mailMessage.setContentIdMap(contentIdMap);
      mailMessage.setTimestamp(timestamp);
      
      if(timestamp > 0){
         mailMessage.setTimestampDate(new Date(timestamp * 1000L));
      }
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
}
