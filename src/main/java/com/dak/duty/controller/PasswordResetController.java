package com.dak.duty.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dak.duty.exception.InvalidIdException;
import com.dak.duty.form.PasswordForgotForm;
import com.dak.duty.form.PasswordResetForm;
import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/passwordReset")
@RequiredArgsConstructor
public class PasswordResetController {

	private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

	private final PersonService personService;
	private final PersonRepository personRepos;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getForgotPassword(final Model model, final HttpServletRequest request) {
		logger.debug("getForgotPassword({})", request.getRemoteAddr());

		model.addAttribute("passwordForgotForm", new PasswordForgotForm());
		return "passwordForgot";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String forgotPassword(final Model model, final HttpServletRequest request, @Valid final PasswordForgotForm form,
			final BindingResult bindingResult) {
		logger.debug("forgotPassword({})", request.getRemoteAddr());

		if (bindingResult.hasErrors()) {
			model.addAttribute("passwordForgotForm", form);
			return "passwordForgot";
		}

		try {
			final String requestUrl = request.getRequestURL().toString();
			this.personService.initiatePasswordReset(form.getEmailAddress(), requestUrl);
			model.addAttribute("success", "You should be receiving an email shortly detailing with a link to reset your password.");
		} catch (final InvalidIdException e) {
			model.addAttribute("passwordForgotForm", form);
			model.addAttribute("error", "No account associated with that email address was found!");
		} catch (final Exception e) {
			model.addAttribute("passwordForgotForm", form);
			model.addAttribute("error", "An unknown error occured");
			logger.error("exception during password reset: {}", e);
		}

		return "passwordForgot";
	}

	@RequestMapping(value = "/{resetToken}", method = RequestMethod.GET)
	public String getResetPassword(@PathVariable("resetToken") final String resetToken, final Model model) {
		logger.debug("resetPassword({})", resetToken);

		final Person person = this.personRepos.findByResetTokenAndResetTokenExpiresGreaterThan(resetToken, LocalDateTime.now());

		if (person == null) {
			model.addAttribute("invalidToken", true);
			model.addAttribute("error", "Invalid reset token - may be expired.");
		}

		model.addAttribute("passwordResetForm", new PasswordResetForm());
		return "passwordReset";
	}

	@RequestMapping(value = "/{resetToken}", method = RequestMethod.POST)
	public String resetPassword(final @PathVariable("resetToken") String resetToken, @Valid final PasswordResetForm form,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request) {
		logger.debug("resetPassword({})", resetToken);

		if (bindingResult.hasErrors() || !form.getPassword().equals(form.getConfirmPassword())) {
			if (form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
				bindingResult.rejectValue("confirmPassword", null, "Passwords must match!");
			}
			model.addAttribute("passwordResetForm", form);
			return "passwordReset";
		}

		final Person person = this.personRepos.findByResetTokenAndResetTokenExpiresGreaterThan(resetToken, LocalDateTime.now());

		if (person == null) {
			model.addAttribute("passwordResetForm", form);
			model.addAttribute("error", "Invalid reset token - may be expired.");
			return "passwordReset";
		}

		if (!form.getEmailAddress().equals(person.getEmailAddress())) {
			model.addAttribute("passwordResetForm", form);
			model.addAttribute("error", "Email address doesn't match what we have on file for this token.");
			return "passwordReset";
		}

		this.personService.setPassword(person, form.getPassword());
		if (this.personService.loginAsPerson(person.getEmailAddress(), form.getPassword(), request)) {
			this.personService.clearResetToken(person);
		}

		return "redirect:/user";
	}
}
