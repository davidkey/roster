package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.dak.duty.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "person_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class PersonRole implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @SequenceGenerator(name = "perrole_id_seq", sequenceName = "perrole_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "perrole_id_seq")
   @Column(nullable = false)
   private long id;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name="person_id", nullable=false)
   @JsonBackReference
   private Person person;
   
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private Role role;
}
