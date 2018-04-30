package com.dak.duty.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.repository.MailMessageRepository;

@Controller
@RequestMapping("/admin/messages")

public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private MailMessageRepository mailMessageRepos;

	@RequestMapping(method = RequestMethod.GET)
	public String getMessages(final Model model) {
		logger.debug("getMessages()");

		model.addAttribute("messages", this.mailMessageRepos.findAllByActiveTrue(new Sort(Sort.Direction.DESC, "timestamp")));
		return "admin/messages";
	}
}
