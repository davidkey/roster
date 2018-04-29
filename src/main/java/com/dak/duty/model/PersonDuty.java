package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "person_duty", uniqueConstraints = { @UniqueConstraint(columnNames = { "person_id", "duty_id" }) })
@Getter
@Setter
public class PersonDuty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "person_duty_id_seq", sequenceName = "person_duty_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "person_duty_id_seq")
	@Column(nullable = false)
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "person_id", nullable = false)
	@JsonBackReference
	private Person person;

	@Column(nullable = false)
	@Min(-1)
	@Max(10)
	private Integer preference = -1; // -1 means NEVER pick me ... EVER!

	@Column(nullable = false)
	@Min(-1)
	@Max(10)
	private Integer adjustedPreference = null;

	@ManyToOne
	@JoinColumn(name = "duty_id", nullable = false)
	private Duty duty;

	@Transient
	public int getWeightedPreference() {
		return this.adjustedPreference == null || this.preference == -1 ? this.preference : this.adjustedPreference;
	}

	@Transient
	public void incrementWeightedPreferenceIfNeeded() {
		if (this.adjustedPreference < this.preference) {
			final int diff = this.preference - this.adjustedPreference;
			if (diff >= 7) {
				this.adjustedPreference += 3;
			} else if (diff >= 4) {
				this.adjustedPreference += 2;
			} else {
				this.adjustedPreference++;
			}
		}
	}

	public void setPreference(final Integer preference) {
		this.preference = preference;
		this.setAdjustedPreference(preference);
	}
}
