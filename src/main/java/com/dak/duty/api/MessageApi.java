package com.dak.duty.api;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.repository.MailMessageRepository;

@Controller
@RequestMapping("/api/message")
@PreAuthorize("hasRole('ROLE_ADMIN')") // will need to open up at least part of this to user so they can "post" messages
													// to admin(s)
public class MessageApi {

	private static final Logger logger = LoggerFactory.getLogger(MessageApi.class);

	@Autowired
	MailMessageRepository messageRepos;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody MailgunMailMessage getMessage(@PathVariable("id") final MailgunMailMessage msg) {
		if (msg.getBodyPlain() != null) {
			msg.setBodyPlain(StringEscapeUtils.escapeHtml4(msg.getBodyPlain()));
		} else {
			msg.setBodyPlain("[no content]");
		}

		return msg;
	}

	@RequestMapping(value = "/{id}/read", method = RequestMethod.POST)
	public @ResponseBody JsonResponse markAsRead(@PathVariable("id") final MailgunMailMessage msg) {
		logger.debug("markAsRead({})", msg.getId());

		msg.setRead(true);
		this.messageRepos.save(msg);
		return new JsonResponse(ResponseStatus.OK, "Message " + msg.getId() + " marked as read");
	}

	@RequestMapping(value = "/{id}/unread", method = RequestMethod.POST)
	public @ResponseBody JsonResponse markAsUnread(@PathVariable("id") final MailgunMailMessage msg) {
		logger.debug("markAsRead({})", msg.getId());

		msg.setRead(false);
		this.messageRepos.save(msg);
		return new JsonResponse(ResponseStatus.OK, "Message " + msg.getId() + " marked as unread");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody JsonResponse delete(@PathVariable("id") final MailgunMailMessage msg) {
		logger.debug("delete({})", msg.getId());

		msg.setActive(false);
		this.messageRepos.save(msg);
		return new JsonResponse(ResponseStatus.OK, "Message " + msg.getId() + " deleted");
	}
}
