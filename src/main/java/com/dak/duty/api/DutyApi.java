package com.dak.duty.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.exception.SortOrderException;
import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.service.DutyService;
import com.dak.duty.service.container.SortOrder;

@Controller
@RequestMapping("/api/duty")
@PreAuthorize("hasRole('ROLE_USER')")
public class DutyApi {

	private static final Logger logger = LoggerFactory.getLogger(DutyApi.class);

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	DutyService dutyService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody JsonResponse delete(@RequestBody Duty duty) {
		logger.debug("duty.delete({})", duty);
		duty = this.dutyService.softDeleteDuty(duty);
		return new JsonResponse(ResponseStatus.OK, "Duty " + duty.getId() + " deleted");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody Duty get(@PathVariable("id") final Long id) {
		logger.debug("duty.get({})", id);
		return this.dutyRepos.findOne(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody JsonResponse save(@RequestBody Duty duty) {
		logger.debug("duty.save({})", duty);
		duty = this.dutyService.saveOrUpdateDuty(duty);
		return new JsonResponse(ResponseStatus.OK, "Duty saved with id " + duty.getId());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/sortOrder", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveSortOrder(@RequestBody final List<SortOrder> params) {
		logger.debug("duty.saveSortOrder({})", params);

		try {
			this.dutyService.updateSortOrder(params);
		} catch (final SortOrderException soe) {
			return new JsonResponse(ResponseStatus.ERROR, soe.getMessage());
		}

		return new JsonResponse(ResponseStatus.OK, "Sort orders updated");
	}

	@RequestMapping(value = "/sortOrder", method = RequestMethod.GET)
	public @ResponseBody List<SortOrder> getSortOrder() {
		logger.debug("duty.getSortOrder({})");
		return this.dutyService.getSortOrders();
	}

}
