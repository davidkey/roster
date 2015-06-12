package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "event_roster_item", uniqueConstraints={@UniqueConstraint(columnNames={"event_id", "duty_id", "person_id"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class EventRosterItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "eri_id_seq", sequenceName = "eri_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "eri_id_seq")
   @Column(nullable = false)
   private long id;
   
   @ManyToOne
   @JoinColumn(name="event_id", nullable = false)
   @JsonBackReference
   private Event event;
   
   @ManyToOne
   @JoinColumn(name="duty_id", nullable = false)
   private Duty duty;
   
   @ManyToOne
   @JoinColumn(name="person_id", nullable = false)
   private Person person;
}
