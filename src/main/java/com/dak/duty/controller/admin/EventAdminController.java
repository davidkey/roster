package com.dak.duty.controller.admin;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.service.EventService;
import com.dak.duty.service.IntervalService;
import com.dak.duty.service.container.EventCalendarNode;

@Controller
@RequestMapping("/admin/events")
// FIXME: move to api (along with jsp calls)
public class EventAdminController {

	private static final Logger logger = LoggerFactory.getLogger(EventAdminController.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private IntervalService intervalService;

	@RequestMapping(value = "/{year}/{month}/json", method = RequestMethod.GET)
	public @ResponseBody List<EventCalendarNode> getEventCalendarItems(@PathVariable("year") final Integer year,
			@PathVariable("month") final Integer month) throws ParseException {
		logger.info("getEventCalendarItems({}, {})", year, month);
		final Date monthDate = new DateTime(year, month, 1, 0, 0, 0).toDate();
		return this.eventService.getEventCalendarNodesForMonth(monthDate);
	}

	@RequestMapping(value = "/all/json", method = RequestMethod.GET)
	public @ResponseBody List<EventCalendarNode> getFutureEventCalendarItems() {
		return this.eventService
				.getAllFutureEventCalendarNodes(this.intervalService.getFirstDayOfMonth(this.intervalService.getCurrentSystemDate()));
	}

	@RequestMapping(value = "/current/json", method = RequestMethod.GET)
	public @ResponseBody List<EventCalendarNode> getCurrentEventCalendarItems() {
		return this.eventService.getEventCalendarNodesForMonth(this.intervalService.getCurrentSystemDate());
	}
}
