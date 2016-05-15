package com.dak.duty.repository.specification;

import java.util.Collection;

import javax.persistence.criteria.Path;

import org.springframework.data.jpa.domain.Specification;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;
import com.dak.duty.model.enums.Role;

public final class PersonSpecs extends AbstractSpecs {

	private PersonSpecs() {

	}

	public static Specification<Person> emailEquals(final String emailAddress) {
		return (root, query, cb) -> cb.equal(root.<Boolean>get("emailAddress"), emailAddress);
	}

	public static Specification<Person> isActive() {
		return (root, query, cb) -> cb.isTrue(root.<Boolean>get("active"));
	}

	public static Specification<Person> isNotActive() {
		return (root, query, cb) -> cb.isFalse(root.<Boolean>get("active"));
	}

	public static Specification<Person> sameOrg() {
		return (root, query, cb) -> cb.equal(root.<Organisation>get("organisation"), AbstractSpecs.getAuthorizedPerson().getOrganisation());
	}

	public static Specification<Person> hasDuty(final Duty duty) {
		return (root, query, cb) -> cb.equal(root.join("duties").<Duty>get("duty"), duty);
	}

	public static Specification<Person> hasRole(final Role role) {
		return (root, query, cb) -> cb.equal(root.join("roles").<Duty>get("role"), role);
	}

	public static Specification<Person> idNotIn(final Collection<Long> personIds) {
		return (root, query, cb) -> {
			final Path<Long> id = root.<Long>get("id");
			return id.in(personIds).not();
		};
	}

	public static Specification<Person> orderByNameLastAscNameFirstAsc() {
		return (root, query, cb) -> {
			query.orderBy(cb.asc(root.<String>get("nameLast")), cb.asc(root.<String>get("nameFirst")));
			return null;
		};
	}

	public static Specification<Person> nameLastLike(final String name) {
		return (root, query, cb) -> {
			final String likePattern = AbstractSpecs.getLikePattern(name);
			return cb.like(cb.lower(root.<String>get("nameLast")), likePattern);
		};
	}

	public static Specification<Person> nameFirstLike(final String name) {
		return (root, query, cb) -> {
			final String likePattern = AbstractSpecs.getLikePattern(name);
			return cb.like(cb.lower(root.<String>get("nameFirst")), likePattern);
		};
	}
}
