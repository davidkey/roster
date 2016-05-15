package com.dak.duty.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.security.CustomUserDetails;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@ToString
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "event_id_seq", sequenceName = "event_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "event_id_seq")
	@Column(nullable = false)
	private long id;

	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	private Organisation organisation;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateEvent;

	@ManyToOne
	@JoinColumn(name = "event_type_id", nullable = false)
	private EventType eventType;

	private String name;

	private Boolean approved = false;

	@OneToMany(mappedBy = "event", orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	@Cascade({ CascadeType.ALL })
	private Set<EventRosterItem> roster;

	@PrePersist
	protected void onPersist() {
		if (this.organisation == null) { // hack?
			this.organisation = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson()
					.getOrganisation();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		if (this.organisation == null) { // hack?
			this.organisation = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson()
					.getOrganisation();
		}
	}

	@Transient
	public boolean isRosterFullyPopulated() {
		return this.eventType != null && this.eventType.getDuties() != null && this.roster != null
				&& this.eventType.getDuties().size() == this.roster.size();
	}

	@Transient
	public boolean isRosterGenerated() {
		return this.roster != null && this.roster.size() > 0;
	}

	@Transient
	public void addEventRosterItem(final EventRosterItem eri) {
		if (eri == null) {
			return;
		}

		if (this.roster == null) {
			this.roster = new HashSet<>();
		}

		eri.setEvent(this);
		this.roster.add(eri);
	}

	@Transient
	public void setEventRoster(final EventRoster er) {
		if (er == null) {
			return;
		}

		if (this.roster != null) {
			this.roster.clear();
		}

		final List<Entry<Duty, Person>> dutiesAndPeople = er.getDutiesAndPeople();

		for (final Entry<Duty, Person> dutyAndPerson : dutiesAndPeople) {
			if (dutyAndPerson.getValue() != null) {
				final EventRosterItem eri = new EventRosterItem();
				eri.setDuty(dutyAndPerson.getKey());
				eri.setPerson(dutyAndPerson.getValue());

				this.addEventRosterItem(eri);
			}
		}
	}
}
