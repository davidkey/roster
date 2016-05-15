package com.dak.duty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.exception.IntervalValidationException;
import com.dak.duty.model.validation.EventTypeIntervalValidation;
import com.dak.duty.security.CustomUserDetails;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "event_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@ToString
public class EventType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "eventtype_id_seq", sequenceName = "eventtype_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "eventtype_id_seq")
	@Column(nullable = false)
	private long id;

	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	private Organisation organisation;

	@Column(nullable = false, unique = true)
	@NotEmpty
	private String name;

	@Column(nullable = true)
	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT) // to prevent dupes... super annoying dupes - see
										// https://stackoverflow.com/questions/17566304/multiple-fetches-with-eager-type-in-hibernate-with-jpa
	private List<Duty> duties;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull // see
				// https://stackoverflow.com/questions/5982741/error-no-validator-could-be-found-for-type-java-lang-integer
	private EventTypeInterval interval;

	@Column(nullable = true)
	private String intervalDetail;

	@Temporal(TemporalType.TIME)
	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
	@DateTimeFormat(pattern = "h:mma") // 12:15am
	private Date startTime = this.getBlankDate();

	@Temporal(TemporalType.TIME)
	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
	@DateTimeFormat(pattern = "h:mma")
	private Date endTime = this.getBlankDate();

	@Column(nullable = false)
	private Boolean active = true;

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
	private Date getBlankDate() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(0L));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public void setInterval(final EventTypeInterval interval) {
		if (this.intervalDetail != null && !EventTypeIntervalValidation.validate(interval, this.intervalDetail)) {
			throw new IntervalValidationException("IntervalDetail '" + this.intervalDetail + "' invalid for type " + interval.toString());
		}

		this.interval = interval;
	}

	public void setIntervalDetail(final String intervalDetail) {
		if (this.interval != null && !EventTypeIntervalValidation.validate(this.interval, intervalDetail)) {
			throw new IntervalValidationException("IntervalDetail '" + intervalDetail + "' invalid for type " + this.interval.toString());
		}

		this.intervalDetail = intervalDetail;
	}

	@Transient
	public void addDuty(final Duty d) {
		if (this.duties == null) {
			this.duties = new ArrayList<>();
		}

		this.duties.add(d);
	}
}
