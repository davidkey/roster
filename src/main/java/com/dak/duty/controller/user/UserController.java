package com.dak.duty.controller.user;

import java.security.Principal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.form.ChangePasswordForm;
import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	PersonRepository personRepos;

	@Autowired
	PersonService personService;

	@Autowired
	IAuthenticationFacade authenticationFacade;

	@RequestMapping(method = RequestMethod.GET)
	public String getUserHome(final Model model, final Principal principal) {
		logger.debug("getUserHome");
		final Person p = this.authenticationFacade.getPerson();

		model.addAttribute("personName", p.getNameFirst() + " " + p.getNameLast());
		return "user/user";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String changePassword(final Model model, final Principal principal) {
		logger.debug("changePassword({})", principal.getName());

		model.addAttribute("changePasswordForm", new ChangePasswordForm());
		return "user/changePassword";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String changePasswordPost(@Valid final ChangePasswordForm form, final BindingResult bindingResult, final Model model,
			final Principal principal, final RedirectAttributes redirectAttributes) {
		logger.debug("changePasswordPost({})", principal.getName());

		final Person person = this.authenticationFacade.getPerson();
		final boolean currentPasswordMatches = this.personService.isCurrentPassword(person, form.getCurrentPassword());

		if (bindingResult.hasErrors() || !form.getNewPassword().equals(form.getNewPasswordConfirm()) || !currentPasswordMatches) {
			if (form.getNewPassword() == null || !form.getNewPassword().equals(form.getNewPasswordConfirm())) {
				bindingResult.rejectValue("newPasswordConfirm", null, "Passwords must match!");
			}

			if (!currentPasswordMatches) {
				bindingResult.rejectValue("currentPassword", null, "Current password invalid!");
			}
			// model.addAttribute("changePasswordForm", form);
			return "user/changePassword";
		}

		// at this point, current password AND new password has been validated ...
		this.personService.setPassword(person, form.getNewPassword());

		redirectAttributes.addFlashAttribute("msg_success", "Password changed!");
		return "redirect:/user";
	}
}
