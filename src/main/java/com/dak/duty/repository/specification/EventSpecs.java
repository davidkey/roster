package com.dak.duty.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.dak.duty.model.Event;

public final class EventSpecs {

	private EventSpecs() {

	}

	public static Specification<Event> isApproved() {

		return (root, query, cb) -> cb.isTrue(root.<Boolean>get("approved"));
	}

	public static Specification<Event> isNotApproved() {

		return (root, query, cb) -> cb.isFalse(root.<Boolean>get("approved"));
	}
}
