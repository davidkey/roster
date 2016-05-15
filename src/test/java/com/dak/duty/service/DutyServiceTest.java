package com.dak.duty.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.exception.SortOrderException;
import com.dak.duty.model.Duty;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.OrganisationRepository;
import com.dak.duty.security.mocking.WithMockCustomUserAdmin;
import com.dak.duty.service.container.SortOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@WithMockCustomUserAdmin
public class DutyServiceTest extends ServiceTest {

	@Autowired
	DutyService dutyService;

	@Autowired
	DutyRepository dutyRepos;

	@Autowired
	OrganisationRepository orgRepos;

	@Test
	public void sortOrderCountShouldMatchDutyCount() {
		Assert.assertTrue("sort order count doesn't match duty count!",
				this.dutyService.getSortOrders().size() == this.dutyRepos.findAll().size());
	}

	@Test(expected = com.dak.duty.exception.SortOrderException.class)
	public void updateSortOrderShouldNotAllowIncompleteInput() throws SortOrderException {
		final List<SortOrder> sortOrders = this.dutyService.getSortOrders();
		Assert.assertFalse("sortOrders shouldn't be null - data not staged correctly?", sortOrders == null);
		Assert.assertFalse("sortOrders shoudn't be empty - data not staged correctly?", sortOrders.isEmpty());

		sortOrders.remove(0);

		this.dutyService.updateSortOrder(sortOrders);
	}

	@Test
	public void testOverallSortOrder() {
		final List<Duty> duties = this.dutyRepos.findByActiveTrue();

		Assert.assertTrue("Not enough duties found for testing!", duties.size() >= 2);

		final Duty minDuty = this.getDutyWithMinSortOrder(), maxDuty = this.getDutyWithMaxSortOrder();

		Assert.assertNotNull(minDuty);
		Assert.assertNotNull(maxDuty);

		Assert.assertTrue("Sort order not populating correctly!", minDuty.getSortOrder() < maxDuty.getSortOrder());
		Assert.assertTrue("Sort order isn't sequential!", maxDuty.getSortOrder() == duties.size());

		final int originalMinSortOrder = minDuty.getSortOrder();
		final int originalMaxSortOrder = maxDuty.getSortOrder();
		minDuty.setSortOrder(originalMaxSortOrder);
		maxDuty.setSortOrder(originalMinSortOrder);

		this.dutyService.saveOrUpdateDuty(minDuty);
		this.dutyService.saveOrUpdateDuty(maxDuty);

		Assert.assertTrue("Sort order not updating correctly", maxDuty.getSortOrder() == originalMinSortOrder);
		Assert.assertTrue("Sort order not updating correctly", minDuty.getSortOrder() == originalMaxSortOrder);

		final Duty newDuty = new Duty();
		newDuty.setDescription("new duty");
		newDuty.setName("new duty");
		newDuty.setSortOrder(1);
		newDuty.setOrganisation(this.orgRepos.findAll().get(0));
		this.dutyService.saveOrUpdateDuty(newDuty);

		Assert.assertTrue("Sort order not incrementing correctly",
				this.dutyRepos.findOne(maxDuty.getId()).getSortOrder() == originalMinSortOrder + 1);
		Assert.assertTrue("Sort order not incrementing correctly",
				this.dutyRepos.findOne(minDuty.getId()).getSortOrder() == originalMaxSortOrder + 1);

		newDuty.setSortOrder(2);
		this.dutyService.saveOrUpdateDuty(newDuty);

		Assert.assertTrue("Sort order not decrementing correctly",
				this.dutyRepos.findOne(maxDuty.getId()).getSortOrder() == originalMinSortOrder);
		Assert.assertTrue("Sort order not decrementing correctly",
				this.dutyRepos.findOne(minDuty.getId()).getSortOrder() == originalMaxSortOrder + 1);
	}

	@Test
	public void testSortOrderInSequence() {
		final List<Duty> activeDuties = this.dutyRepos.findByActiveTrue();

		Assert.assertNotNull(activeDuties);
		Assert.assertTrue("Not enough test data!", activeDuties.size() > 1);
		Assert.assertTrue("Sort orders not sequencing correctly!", this.areSortOrdersInSequence(activeDuties));
	}

	@Test
	public void testBrokenSequenceVerification() {
		Duty duty = new Duty();
		duty.setName("testBrokenSequenceVerification");
		duty.setOrganisation(this.orgRepos.findAll().get(0));
		duty.setSortOrder(this.dutyRepos.findMaxSortOrder() + 2); // out of sequence
		duty = this.dutyService.saveOrUpdateDuty(duty);

		Assert.assertFalse("Sort order sequence verification didn't detect out-of-sequence sort orders!",
				this.areSortOrdersInSequence(this.dutyRepos.findByActiveTrue()));
	}

	@Test
	public void testSortOrderAfterSoftDeleteOfFirstSortOrder() {
		Duty duty = new Duty();
		duty.setName("delete me please");
		duty.setOrganisation(this.orgRepos.findAll().get(0));
		duty.setSortOrder(1);

		duty = this.dutyService.saveOrUpdateDuty(duty);

		duty.setActive(false);
		duty = this.dutyService.saveOrUpdateDuty(duty);

		Assert.assertTrue("Sort orders not sequencing correctly after soft delete!",
				this.areSortOrdersInSequence(this.dutyRepos.findByActiveTrue()));
	}

	@Test
	public void testSortOrderAfterSoftDeleteOfLastSortOrder() {
		Duty duty = new Duty();
		duty.setName("delete me please");
		duty.setOrganisation(this.orgRepos.findAll().get(0));
		duty.setSortOrder(this.dutyRepos.findMaxSortOrder() + 1);

		duty = this.dutyService.saveOrUpdateDuty(duty);

		duty.setActive(false);
		duty = this.dutyService.saveOrUpdateDuty(duty);

		Assert.assertTrue("Sort orders not sequencing correctly after soft delete!",
				this.areSortOrdersInSequence(this.dutyRepos.findByActiveTrue()));
	}

	private boolean areSortOrdersInSequence(final List<Duty> duties) {
		final Set<Integer> sortOrders = new HashSet<>();

		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		for (final Duty d : duties) {
			final int sortOrder = d.getSortOrder();

			if (sortOrders.contains(sortOrder)) {
				return false;
			}

			sortOrders.add(sortOrder);

			if (sortOrder < min) {
				min = sortOrder;
			}

			if (sortOrder > max) {
				max = sortOrder;
			}
		}

		return duties.isEmpty() || (min == 1 && max == sortOrders.size());
	}

	private Duty getDutyWithMinSortOrder() {
		final List<Duty> duties = this.dutyRepos.findAll();

		Duty duty = null;
		for (final Duty d : duties) {
			if (duty == null) {
				duty = d;
			} else {
				if (duty.getSortOrder() > d.getSortOrder()) {
					duty = d;
				}
			}
		}

		return duty;
	}

	private Duty getDutyWithMaxSortOrder() {
		final List<Duty> duties = this.dutyRepos.findAll();

		Duty duty = null;
		for (final Duty d : duties) {
			if (duty == null) {
				duty = d;
			} else {
				if (duty.getSortOrder() < d.getSortOrder()) {
					duty = d;
				}
			}
		}

		return duty;
	}

}
