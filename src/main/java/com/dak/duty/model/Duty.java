package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.security.CustomUserDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "duty", uniqueConstraints={@UniqueConstraint(columnNames = {"org_id", "name"})})
@Getter
@Setter
@ToString
public class Duty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "duty_id_seq", sequenceName = "duty_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "duty_id_seq")
	@Column(nullable = false)
	private long id;

	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	private Organisation organisation;

	@Column(nullable = false)
	@NotEmpty
	private String name;

	@Column(nullable = true)
	private String description;

	@Min(1)
	@Column(nullable = false)
	private Integer sortOrder = 1;

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

	public void setName(String name) {
		if (name != null) {
			name = name.trim();
		}

		this.name = name;
	}

	/**
	 * hashcode and equals exclude organisation!
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Duty other = (Duty) obj;
		if (this.active == null) {
			if (other.active != null) {
				return false;
			}
		} else if (!this.active.equals(other.active)) {
			return false;
		}
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.sortOrder == null) {
			if (other.sortOrder != null) {
				return false;
			}
		} else if (!this.sortOrder.equals(other.sortOrder)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.active == null) ? 0 : this.active.hashCode());
		result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.sortOrder == null) ? 0 : this.sortOrder.hashCode());
		return result;
	}

}
