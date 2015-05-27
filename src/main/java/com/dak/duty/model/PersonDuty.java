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
   @SequenceGenerator(name = "person_duty_id_seq", sequenceName = "person_duty_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "person_duty_id_seq")
   @Column(nullable = false)
   private long id;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name="person_id", nullable=false)
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
   @JoinColumn(name="duty_id", nullable=false)
   private Duty duty;
   
   
   @Transient
   public int getWeightedPreference(){
      return adjustedPreference == null ? preference : adjustedPreference;
   }
   
   @Transient
   public void incrementWeightedPreferenceIfNeeded(){
      if(adjustedPreference < preference){
         final int diff = preference - adjustedPreference;
         if(diff >= 7){
            adjustedPreference += 3;
         } else if(diff >= 4){
            adjustedPreference += 2;
         } else {
            adjustedPreference++;
         }
      }
   }
   
   public void setPreference(final Integer preference){
      this.preference = preference;
      setAdjustedPreference(preference);
   }
}
