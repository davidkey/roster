package com.dak.duty.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "organisation")
@Getter
@Setter
public class Organisation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "org_id_seq", sequenceName = "org_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "org_id_seq")
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String registrationCode;

	@Column(nullable = false)
	private LocalDateTime dateCreated = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime dateUpdated = LocalDateTime.now();

	@PreUpdate
	protected void onUpdate() {
		this.dateUpdated = LocalDateTime.now();
	}
}
