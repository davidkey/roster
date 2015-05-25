package com.dak.duty.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
public class Person  implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "person_id_seq", sequenceName = "person_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "person_id_seq")
   @Column(nullable = false)
   private long id;

   @Column(nullable = false)
   @NotEmpty
   private String nameFirst;

   @Column(nullable = false)
   @NotEmpty
   private String nameLast;

   @Column(nullable = true) // can either be null OR a valid email address
   @Email
   private String emailAddress;

   @Column(nullable = false)
   private Boolean active = true;

   @OneToMany(mappedBy="person", fetch=FetchType.EAGER)
   @JsonManagedReference
   @Cascade({CascadeType.ALL})
   @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
   private Set<PersonDuty> duties;
   
   @Transient
   public int getPreferenceForDuty(Duty d){
      for(PersonDuty pd : duties){
         if(pd.getDuty() != null && pd.getDuty().getId() == d.getId()){
            return pd.getPreference();
         }
      }
      return -1;
   }

   @Transient
   public void addPersonDuty(final PersonDuty pd){
      if(duties == null){
         duties = new HashSet<PersonDuty>();
      }

      pd.setPerson(this);
      duties.add(pd);
   }

   /**
    * This method will become private - use {@link #addOrUpdateDutyAndPreference(Duty, Integer) addOrUpdateDutyAndPreference} instead.
    * @param duty
    * @param preference
    */
   @Transient
   @Deprecated
   public void addDutyAndPreference(final Duty duty, final Integer preference){
      PersonDuty pd = new PersonDuty();
      pd.setDuty(duty);
      pd.setPreference(preference);
      this.addPersonDuty(pd);
   }

   /**
    * Adds (or updates) duty and preference pair for this {@link #Person Person}.
    * Note that if preference is -1, it will not be added, only updated if the duty
    * already exists.
    * @param duty
    * @param preference
    */
   @Transient
   public void addOrUpdateDutyAndPreference(final Duty duty, final Integer preference){
      boolean found = false;
      
      if(duties != null){
         for(PersonDuty pd : duties){
            if(pd.getDuty() != null && pd.getDuty().getId() == duty.getId()){
               found = true;
               pd.setPreference(preference);
               break;
            }
         }
      }
         
      if(!found){
         if(preference > -1){ // don't bother saving 'never pick me' preference
            PersonDuty pd = new PersonDuty();
            pd.setDuty(duty);
            pd.setPreference(preference);
            this.addPersonDuty(pd);
         }
      }
   }

}
