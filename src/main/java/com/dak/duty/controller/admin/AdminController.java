package com.dak.duty.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.service.InitialisationService;
import com.dak.duty.service.VersionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	private final InitialisationService initService;
	private final VersionService versionService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAdminHome(final Model model) {
		logger.debug("getAdminHome()");
		return "admin/admin";
	}

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public String getSettings(final Model model) {
		logger.debug("getSettings()");
		return "admin/settings";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(final Model model) {
		logger.debug("getAbout()");
		
		model.addAttribute("version", versionService.getVersion());
		model.addAttribute("commitId", versionService.getCommitId());
		model.addAttribute("buildTimestamp", versionService.getTimestamp());
		return "admin/about";
	}

	@RequestMapping("/init")
	public @ResponseBody String initData() {
		this.initService.populateDefaultData();
		return "data init'd";
	}

}
