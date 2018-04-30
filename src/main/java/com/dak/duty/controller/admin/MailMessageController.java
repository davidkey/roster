package com.dak.duty.controller.admin;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.repository.MailMessageRepository;
import com.dak.duty.service.EmailService;

@Controller
@RequestMapping("/mail")
public class MailMessageController {

	@Autowired
	private MailMessageRepository mailMessageRepos;

	@Autowired
	private EmailService<MailgunMailMessage> emailService;

	private static final Logger logger = LoggerFactory.getLogger(MailMessageController.class);

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Boolean postMessage(@ModelAttribute @Valid final MailgunMailMessage mailgunMailMessage) {
		logger.debug("postMessage({})", mailgunMailMessage);

		if (!this.emailService.validateIncoming(mailgunMailMessage)) {
			throw new MailValidationException("Email validation failed!");
		}

		this.mailMessageRepos.save(mailgunMailMessage);
		return true;
	}

	@InitBinder(value = "mailgunMailMessage")
	private void bind(final WebDataBinder dataBinder, final WebRequest webRequest,
			@RequestParam(value = "body-plain", required = false) final String bodyPlain,
			@RequestParam(value = "stripped-text", required = false) final String strippedText,
			@RequestParam(value = "stripped-signature", required = false) final String strippedSignature,
			@RequestParam(value = "body-html", required = false) final String bodyHtml,
			@RequestParam(value = "stripped-html", required = false) final String strippedHtml,
			@RequestParam(value = "attachment-count", required = false) final Integer attachmentCount,
			@RequestParam(value = "attachment-x", required = false) final String attachementX,
			@RequestParam(value = "message-headers", required = false) final String messageHeaders,
			@RequestParam(value = "content-id-map", required = false) final String contentIdMap,
			@RequestParam(value = "timestamp", required = false) final Integer timestamp) {

		final MailgunMailMessage mailgunMailMessage = (MailgunMailMessage) dataBinder.getTarget();

		mailgunMailMessage.setBodyPlain(bodyPlain);
		mailgunMailMessage.setStrippedText(strippedText);
		mailgunMailMessage.setStrippedSignature(strippedSignature);
		mailgunMailMessage.setBodyHtml(bodyHtml);
		mailgunMailMessage.setStrippedHtml(strippedHtml);
		mailgunMailMessage.setAttachmentCount(attachmentCount == null ? 0 : attachmentCount);
		mailgunMailMessage.setAttachementX(attachementX);
		mailgunMailMessage.setMessageHeaders(messageHeaders);
		mailgunMailMessage.setContentIdMap(contentIdMap);

		if (timestamp != null) {
			mailgunMailMessage.setTimestamp(timestamp);
			//mailgunMailMessage.setTimestampDate(new Date(timestamp * 1000L));
			mailgunMailMessage.setTimestampDate(LocalDateTime.ofEpochSecond(timestamp * 1000L * 1000L, 0, ZoneOffset.UTC));
		}
	}
}
