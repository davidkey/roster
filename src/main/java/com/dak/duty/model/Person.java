package com.dak.duty.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "person", uniqueConstraints={@UniqueConstraint(columnNames={"emailAddress"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
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

   @Column(nullable = true) // can either be null OR a valid email address - if null, won't be able to log in as email=username
   @Email
   private String emailAddress;
   
   @Column(nullable=false)
   private String password = "NOT_A_REAL_PASSWORD";

   @Column(nullable = false)
   private Boolean active = true;
   
   @Column(nullable = false)
   @Temporal(TemporalType.TIMESTAMP)
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm aaa")
   private Date lastUpdated = new Date();

   @OneToMany(mappedBy="person", fetch=FetchType.EAGER)
   @JsonManagedReference
   @Cascade({CascadeType.ALL})
   @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
   private Set<PersonDuty> duties;
   
   @OneToMany(mappedBy="person", fetch=FetchType.EAGER)
   @JsonManagedReference
   @Cascade({CascadeType.ALL})
   @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
   private Set<PersonRole> roles;
   
   @PreUpdate
   protected void onUpdate() {
      lastUpdated = new Date();
   }
   
   @Transient 
   public void addRole(final PersonRole personRole){
      if(roles == null){
         roles = new HashSet<PersonRole>();
      }
      
      personRole.setPerson(this);
      roles.add(personRole);
   }
   
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

   @Transient
   public void addDutyAndPreference(final Duty duty, final Integer preference){
      PersonDuty pd = new PersonDuty();
      pd.setDuty(duty);
      pd.setPreference(preference);
      setLastUpdated(new Date());
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
               setLastUpdated(new Date());
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
