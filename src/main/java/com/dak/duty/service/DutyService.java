package com.dak.duty.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.exception.SortOrderException;
import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.container.SortOrder;

@Service
@Transactional
public class DutyService {

	private final DutyRepository dutyRepos;
	private final IAuthenticationFacade authenticationFacade;
	private final DateTimeFormatter fmt;
	
	@Autowired
	public DutyService(final DutyRepository dutyRepos, final IAuthenticationFacade authenticationFacade) {
		this.dutyRepos = dutyRepos;
		this.authenticationFacade = authenticationFacade;
		this.fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	}

	public Duty saveOrUpdateDuty(final Duty duty) {

		if (duty.getOrganisation() == null) {
			duty.setOrganisation(this.authenticationFacade.getOrganisation());
		} else if (!duty.getOrganisation().getId().equals(this.authenticationFacade.getOrganisation().getId())) {
			throw new RosterSecurityException("can't do that");
		}

		/**
		 * If this is a soft delete: 1) Update name accordingly, decrement sort order [>=] current duty.sortOrder
		 *
		 * Otherwise (update or a new duty) 
		 * 1) Decrement all sort orders [>] old sort order (if this is an update, not a new item) 
		 * 2) Increment all sort orders [>=] new / updated duty sort order IF this isn't an inactive duty 
		 * 3) Persist duty w/ new sort order
		 */
		if (duty.getId() > 0) { // if this is an update, not a new entity
			final Duty dutyBeforeChanges = this.dutyRepos.findOne(duty.getId());

			if (!duty.getActive() && dutyBeforeChanges.getActive()) { // if we're deactivating / soft deleting this duty...
				duty.setName(duty.getName() + " (deleted @ " + LocalDateTime.now().format(fmt) + "; id " + duty.getId() + ")"); // change name to show soft  delete and to prevent key errors if another with same name added later
				this.dutyRepos.decrementSortOrderAboveAndIncludingExcludingDutyId(dutyBeforeChanges.getSortOrder(), duty.getId());
			}
			
			if (duty.getActive() && !duty.getSortOrder().equals(dutyBeforeChanges.getSortOrder())) {
				this.dutyRepos.decrementSortOrderAboveExcludingDutyId(dutyBeforeChanges.getSortOrder(), duty.getId());
				this.dutyRepos.incrementSortOrderAboveAndIncludingExcludingDutyId(duty.getSortOrder(), duty.getId());
			}
		} else {
			this.dutyRepos.incrementSortOrderAboveAndIncluding(duty.getSortOrder());
		}

		return this.dutyRepos.save(duty);
	}

	public void updateSortOrder(final List<SortOrder> sortOrders) throws SortOrderException {
		final Map<Long, Integer> sortOrderSet = SortOrder.getSortMap(sortOrders);
		final List<Duty> allActiveDuties = this.dutyRepos.findByActiveTrue();
		final List<Duty> dutiesToUpdate = new ArrayList<>();

		int minSortOrder = Integer.MAX_VALUE;
		int maxSortOrder = Integer.MIN_VALUE;

		for (final Duty duty : allActiveDuties) {
			if (sortOrderSet.containsKey(duty.getId())) {
				final int newSortOrder = sortOrderSet.get(duty.getId());
				final int oldSortOrder = duty.getSortOrder();

				if (newSortOrder != oldSortOrder) {
					duty.setSortOrder(newSortOrder);
					dutiesToUpdate.add(duty);
				}
				
				minSortOrder = Math.min(newSortOrder, minSortOrder);
				maxSortOrder = Math.max(newSortOrder, maxSortOrder);
			} else {
				throw new SortOrderException("Duty " + duty.getId() + " was not included in sort.");
			}
		}

		if (minSortOrder != 1 || maxSortOrder != allActiveDuties.size()) {
			throw new SortOrderException("Invalid sort order sequence");
		}

		if (!dutiesToUpdate.isEmpty()) {
			this.dutyRepos.saveAll(dutiesToUpdate);
		}
	}

	public List<SortOrder> getSortOrders() {
		return this.dutyRepos.findByActiveTrue().stream()
			.map(SortOrder::fromDuty)
			.collect(Collectors.toList());
	}

	public Duty softDeleteDuty(Duty duty) {
		duty = this.dutyRepos.findOne(duty.getId());
		duty.setActive(false);
		duty.setSortOrder(1);
		duty = this.saveOrUpdateDuty(duty);
		return duty;
	}
}
