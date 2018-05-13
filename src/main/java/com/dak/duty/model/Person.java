package com.dak.duty.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.security.CustomUserDetails;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "person", uniqueConstraints = { @UniqueConstraint(columnNames = { "nameFirst", "nameLast", "org_id" }) })
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "person_id_seq", sequenceName = "person_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "person_id_seq")
	@Column(nullable = false)
	private long id;

	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	private Organisation organisation;

	@Column(nullable = false)
	@NotEmpty
	private String nameFirst;

	@Column(nullable = false)
	@NotEmpty
	private String nameLast;

	@Column(nullable = true) // can either be null OR a valid email address - if null, won't be able to log in as email=username
	@Email
	private String emailAddress;

	@Column(nullable = false, length = 60)
	private String password = "NOT_A_REAL_PASSWORD";

	@Column(nullable = false)
	private Boolean active = true;

	@Column(nullable = false)
	//@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a")
	private LocalDateTime lastUpdated = LocalDateTime.now();

	@OneToMany(mappedBy = "person", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference
	private Set<PersonDuty> duties;

	@OneToMany(mappedBy = "person", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference
	private Set<PersonRole> roles;

	@Column(nullable = true)
	private String resetToken;

	@Column(nullable = true)
	private LocalDateTime resetTokenExpires;

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

	@JsonIgnore
	public String getPassword() {
		return this.password;
	}

	@JsonProperty
	public void setPassword(final String password) {
		this.password = password;
	}

	@Transient
	public void addRole(final PersonRole personRole) {
		if (this.roles == null) {
			this.roles = new HashSet<>();
		}

		personRole.setPerson(this);
		this.roles.add(personRole);
	}

	@Transient
	public void addRoles(final List<PersonRole> roles) {
		if (roles != null) {
			for (final PersonRole pr : roles) {
				this.addRole(pr);
			}
		}
	}

	@Transient
	public int getPreferenceForDuty(final Duty d) {
		for (final PersonDuty pd : this.duties) {
			if (pd.getDuty() != null && pd.getDuty().getId() == d.getId()) {
				return pd.getPreference();
			}
		}
		return -1;
	}

	@Transient
	public void addPersonDuty(final PersonDuty pd) {
		if (this.duties == null) {
			this.duties = new HashSet<>();
		}

		pd.setPerson(this);
		this.duties.add(pd);
	}

	@Transient
	public void addDutyAndPreference(final Duty duty, final Integer preference) {
		final PersonDuty pd = new PersonDuty();
		pd.setDuty(duty);
		pd.setPreference(preference);
		this.setLastUpdated(LocalDateTime.now());
		this.addPersonDuty(pd);
	}

	/**
	 * Adds (or updates) duty and preference pair for this {@link #Person Person}. Note that if preference is -1, it will
	 * not be added, only updated if the duty already exists.
	 * @param duty
	 * @param preference
	 */
	@Transient
	public void addOrUpdateDutyAndPreference(final Duty duty, final Integer preference) {
		boolean found = false;

		if (this.duties != null) {
			for (final PersonDuty pd : this.duties) {
				if (pd.getDuty() != null && pd.getDuty().getId() == duty.getId()) {
					found = true;
					pd.setPreference(preference);
					this.setLastUpdated(LocalDateTime.now());
					break;
				}
			}
		}

		if (!found) {
			if (preference > -1) { // don't bother saving 'never pick me' preference
				final PersonDuty pd = new PersonDuty();
				pd.setDuty(duty);
				pd.setPreference(preference);
				this.addPersonDuty(pd);
			}
		}
	}

}
