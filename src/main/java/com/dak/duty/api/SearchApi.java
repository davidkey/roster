package com.dak.duty.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.AutocompleteNode;
import com.dak.duty.api.util.AutocompleteResponse;
import com.dak.duty.model.Duty;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;

@Controller
@RequestMapping("/api/search")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SearchApi {

	private static final Logger logger = LoggerFactory.getLogger(SearchApi.class);

	@Autowired
	PersonRepository personRepos;

	@Autowired
	EventTypeRepository eventTypeRepos;

	@Autowired
	DutyRepository dutyRepos;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody AutocompleteResponse getSearch(@RequestParam("query") final String searchString) {
		logger.debug("getSearch({})", searchString);

		final List<AutocompleteNode> nodes = new ArrayList<>();

		// find and add people
		final List<Person> people = this.personRepos.findAll(
					PersonSpecs.isActive()
					.and(PersonSpecs.sameOrg())
					.and(PersonSpecs.nameFirstLike(searchString).or(PersonSpecs.nameLastLike(searchString)))
				);
		
		
		for (final Person p : people) {
			nodes.add(new AutocompleteNode(p.getNameLast() + ", " + p.getNameFirst(), "/admin/people/" + p.getId()));
		}

		// find and add event types
		final List<EventType> eventTypes = this.eventTypeRepos.findByNameContainsIgnoreCaseAndActiveTrue(searchString);
		for (final EventType et : eventTypes) {
			nodes.add(new AutocompleteNode(et.getName(), "/admin/eventTypes/" + et.getId()));
		}

		// find and add duties
		final List<Duty> duties = this.dutyRepos.findByNameContainsIgnoreCaseAndActiveTrue(searchString);
		for (final Duty d : duties) {
			nodes.add(new AutocompleteNode(d.getName(), "/admin/duties/" + d.getId()));
		}

		return new AutocompleteResponse(searchString, nodes);
	}
}
