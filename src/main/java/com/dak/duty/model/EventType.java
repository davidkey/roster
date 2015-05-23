package com.dak.duty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.dak.duty.model.enums.EventTypeInterval;

@Entity
@Table(name = "event_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class EventType implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "eventtype_id_seq", sequenceName = "eventtype_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "eventtype_id_seq")
   @Column(nullable = false)
   private long id;
   
   @Column(nullable = false, unique = true)
   private String name;
   
   @Column(nullable = true)
   private String description;
   
   @ManyToMany(fetch=FetchType.EAGER)
   private List<Duty> duties;
   
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private EventTypeInterval interval;
   
   @Transient
   public void addDuty(final Duty d){
      if(duties == null){
         duties = new ArrayList<Duty>();
      }
      
      duties.add(d);
   }
}
