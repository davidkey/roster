package com.dak.duty.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Event implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @SequenceGenerator(name = "event_id_seq", sequenceName = "event_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "event_id_seq")
   @Column(nullable = false)
   private long id;
   
   @Temporal(TemporalType.DATE)
   @Column(nullable = false)
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
   private Date dateEvent;
   
   @ManyToOne
   @JoinColumn(name="event_type_id", nullable=false)
   private EventType eventType;
   
   private String name;

}
