package com.dak.duty.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.PersonService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user/preferences")
@RequiredArgsConstructor
public class PreferencesController {

	private static final Logger logger = LoggerFactory.getLogger(PreferencesController.class);

	private final DutyRepository dutyRepos;
	private final PersonService personService;
	private final PersonRepository personRepos;
	private final IAuthenticationFacade authenciationFacade;

	@RequestMapping(method = RequestMethod.GET)
	public String getPreferences(final Model model) {

		model.addAttribute("person", this.personRepos.findOne(this.authenciationFacade.getPerson().getId()));
		model.addAttribute("duties", this.dutyRepos.findAllByActiveTrueOrderByNameAsc());

		return "user/preferences";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String savePreferences(final Model model, @RequestParam final MultiValueMap<String, String> parameters,
			final RedirectAttributes redirectAttributes) {
		logger.debug("savePreferences()");

		this.personService.updateDutiesFromFormPost(this.personRepos.findOne(this.authenciationFacade.getPerson().getId()), parameters);
		redirectAttributes.addFlashAttribute("msg_success", "Duties updated!");
		return "redirect:/user/preferences";
	}
}
