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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "person_duty")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class PersonDuty implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @SequenceGenerator(name = "duty_id_seq", sequenceName = "duty_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "duty_id_seq")
   @Column(nullable = false)
   private long id;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name="person_id", nullable=false)
   @JsonBackReference
   private Person person;
   
   @Column(nullable = false)
   @Min(0)
   @Max(10)
   private Integer preference = 0;

   @ManyToOne
   @JoinColumn(name="duty_id", nullable=false)
   private Duty duty;
}
