package com.dak.duty.controller.admin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.repository.MailMessageRepository;
import com.dak.duty.service.EmailService;
import com.dak.duty.service.ServiceTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class MailMessageControllerTest extends ServiceTest {

	@Mock
	private MailMessageRepository mailMessageRepos;

	@Mock
	private EmailService<MailgunMailMessage> emailService;

	//@InjectMocks
	private MailMessageController mailMessageController;

	private MockMvc mockMvc;

	private final String BODY_PLAIN = "body plain text";
	private final String STRIPPED_TEXT = "some stripped text";
	private final String STRIPPED_SIGNATURE = "stripped sig";
	private final String BODY_HTML = "<h1>some html</h1>";
	private final String STRIPPED_HTML = this.BODY_HTML + "<br/>";
	private final int ATTACHMENT_COUNT = 2;
	private final String ATTACHMENT_X = "attachment x text";
	private final String MESSAGE_HEADERS = "some random header";
	private final String CONTENT_ID_MAP = "{1,2,3}";
	private final int TIMESTAMP = 10000;

	@Before
	public void setup() {
		mailMessageController = new MailMessageController(mailMessageRepos, emailService);
		
		//MockitoAnnotations.initMocks(this);

		this.mockMvc = MockMvcBuilders.standaloneSetup(this.mailMessageController).build();
	}

	@Test
	public void testMailMessageBinding() throws Exception {

		// skip validation (auto-true)
		Mockito.when(this.emailService.validateIncoming(ArgumentMatchers.any(MailgunMailMessage.class))).thenReturn(Boolean.TRUE);

		// capture message when .save action occurs
		final ArgumentCaptor<MailgunMailMessage> msg = ArgumentCaptor.forClass(MailgunMailMessage.class);

		// post a message using every "special" field (with dashes)
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/mail").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("body-plain", this.BODY_PLAIN).param("stripped-text", this.STRIPPED_TEXT)
						.param("stripped-signature", this.STRIPPED_SIGNATURE).param("body-html", this.BODY_HTML)
						.param("stripped-html", this.STRIPPED_HTML).param("attachment-count", String.valueOf(this.ATTACHMENT_COUNT))
						.param("attachment-x", this.ATTACHMENT_X).param("message-headers", this.MESSAGE_HEADERS)
						.param("content-id-map", this.CONTENT_ID_MAP).param("timestamp", String.valueOf(this.TIMESTAMP)))
				.andExpect(MockMvcResultMatchers.status().isOk());

		Mockito.verify(this.mailMessageRepos).save(msg.capture());

		final MailgunMailMessage savedMsg = msg.getValue();
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getBodyPlain().equals(this.BODY_PLAIN));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getStrippedText().equals(this.STRIPPED_TEXT));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getStrippedSignature().equals(this.STRIPPED_SIGNATURE));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getBodyHtml().equals(this.BODY_HTML));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getStrippedHtml().equals(this.STRIPPED_HTML));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getAttachmentCount() == this.ATTACHMENT_COUNT);
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getAttachementX().equals(this.ATTACHMENT_X));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getMessageHeaders().equals(this.MESSAGE_HEADERS));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getContentIdMap().equals(this.CONTENT_ID_MAP));
		Assert.assertTrue("MailMessage binding not working correctly!", savedMsg.getTimestamp() == this.TIMESTAMP);
	}
}
