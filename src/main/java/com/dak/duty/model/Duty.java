package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "duty")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Duty implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @SequenceGenerator(name = "duty_id_seq", sequenceName = "duty_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "duty_id_seq")
   @Column(nullable = false)
   private long id;
   
   @Column(nullable = false)
   private String name;
   
   @Column(nullable = true)
   private String description;

}
