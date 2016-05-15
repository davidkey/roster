package com.dak.duty.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.service.DutyService;

@Controller
@RequestMapping("/admin/duties")
public class DutyAdminController {

	private static final Logger logger = LoggerFactory.getLogger(DutyAdminController.class);

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	DutyService dutyService;

	@RequestMapping(method = RequestMethod.GET)
	public String getDuties(final Model model) {
		DutyAdminController.logger.debug("getDuties()");

		final List<Duty> duties = this.dutyRepos.findByActiveTrueOrderBySortOrderAsc();
		DutyAdminController.logger.debug("duties found: {}", duties.size());

		model.addAttribute("duties", duties);
		return "admin/duties";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveDuty(@ModelAttribute @Valid final Duty duty, final BindingResult result, final RedirectAttributes redirectAttributes) {
		DutyAdminController.logger.debug("saveDuty()");
		final boolean alreadyExisted = duty.getId() > 0;

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.duty", result);
			redirectAttributes.addFlashAttribute("duty", duty);

			if (alreadyExisted) {
				return "redirect:/admin/duties/" + duty.getId();
			} else {
				return "redirect:/admin/duties/new";
			}
		}

		this.dutyService.saveOrUpdateDuty(duty);
		redirectAttributes.addFlashAttribute("msg_success", alreadyExisted ? "Duty updated!" : "Duty added!");
		return "redirect:/admin/duties";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNewDuty(final Model model) {
		DutyAdminController.logger.debug("getNewDuty()");

		if (!model.containsAttribute("duty")) {
			final Duty duty = new Duty();
			final Integer maxSortOrder = this.dutyRepos.findMaxSortOrder();
			duty.setSortOrder((maxSortOrder == null ? 0 : maxSortOrder) + 1);

			model.addAttribute("duty", duty);
		}

		return "admin/duty";
	}

	@PreAuthorize("#d.organisation.id == principal.person.organisation.id")
	@RequestMapping(value = "/{dutyId}", method = RequestMethod.GET)
	public String getEditDuty(@PathVariable("dutyId") @P("d") final Duty duty, final Model model) {
		DutyAdminController.logger.debug("getEditDuty()");

		if (!model.containsAttribute("duty")) {
			model.addAttribute("duty", duty);
		}

		model.addAttribute("maxSortOrder", this.dutyRepos.findMaxSortOrder());
		return "admin/duty";
	}
}
