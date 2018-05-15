package com.dak.duty.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(final Locale locale, final Model model) {
		return "home";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLogin(@RequestParam(value = "error", required = false) final String error, final Model model,
			final HttpServletRequest request) {

		logger.debug("getLogin() by ip {}", request.getRemoteAddr());

		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}

		return "login";
	}
}
