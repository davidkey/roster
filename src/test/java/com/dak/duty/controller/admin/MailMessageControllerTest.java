package com.dak.duty.controller.admin;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // <-- this is it
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.repository.MailMessageRepository;
import com.dak.duty.service.EmailService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/servlet-context-test.xml"})
public class MailMessageControllerTest {
   
   @Mock
   MailMessageRepository mailMessageRepos;
   
   @Mock
   private EmailService<MailgunMailMessage> emailService;
   
   @InjectMocks
   private MailMessageController mailMessageController;

   private MockMvc mockMvc;
   
   private final String BODY_PLAIN = "body plain text";
   private final String STRIPPED_TEXT = "some stripped text";
   private final String STRIPPED_SIGNATURE = "stripped sig";
   private final String BODY_HTML = "<h1>some html</h1>";
   private final String STRIPPED_HTML = BODY_HTML + "<br/>";
   private final int ATTACHMENT_COUNT = 2;
   private final String ATTACHMENT_X = "attachment x text";
   private final String MESSAGE_HEADERS = "some random header";
   private final String CONTENT_ID_MAP = "{1,2,3}";
   private final int TIMESTAMP = 10000;
   
   @Before
   public void setup() {
       MockitoAnnotations.initMocks(this);
       this.mockMvc = MockMvcBuilders.standaloneSetup(mailMessageController).build();
   }
   
   @Test
   public void testMailMessageBinding() throws Exception{

      // skip validation (auto-true)
      Mockito.when(emailService.validateIncoming((MailgunMailMessage)notNull())).thenReturn(true);
      
      // capture message when .save action occurs
      ArgumentCaptor<MailgunMailMessage> msg = ArgumentCaptor.forClass(MailgunMailMessage.class);
      
      // post a message using every "special" field (with dashes)
      mockMvc.perform(post("/mail")
               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
               .param("body-plain", BODY_PLAIN)
               .param("stripped-text", STRIPPED_TEXT)
               .param("stripped-signature", STRIPPED_SIGNATURE)
               .param("body-html", BODY_HTML)
               .param("stripped-html", STRIPPED_HTML)
               .param("attachment-count", String.valueOf(ATTACHMENT_COUNT))
               .param("attachment-x", ATTACHMENT_X)
               .param("message-headers", MESSAGE_HEADERS)
               .param("content-id-map", CONTENT_ID_MAP)
               .param("timestamp", String.valueOf(TIMESTAMP))
            ).andExpect(status().isOk());
      
      verify(mailMessageRepos).save(msg.capture());
      
      final MailgunMailMessage savedMsg = msg.getValue();
      assertTrue("MailMessage binding not working correctly!", savedMsg.getBodyPlain().equals(BODY_PLAIN));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getStrippedText().equals(STRIPPED_TEXT));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getStrippedSignature().equals(STRIPPED_SIGNATURE));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getBodyHtml().equals(BODY_HTML));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getStrippedHtml().equals(STRIPPED_HTML));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getAttachmentCount() == ATTACHMENT_COUNT);
      assertTrue("MailMessage binding not working correctly!", savedMsg.getAttachementX().equals(ATTACHMENT_X));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getMessageHeaders().equals(MESSAGE_HEADERS));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getContentIdMap().equals(CONTENT_ID_MAP));
      assertTrue("MailMessage binding not working correctly!", savedMsg.getTimestamp() == TIMESTAMP);
   }
}
