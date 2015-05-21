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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Person  implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "person_id_seq", sequenceName = "person_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "person_id_seq")
   @Column(nullable = false)
   private long id;
   
   @Column(nullable = false)
   private String nameFirst;
   
   @Column(nullable = false)
   private String nameLast;
   
   @Column(nullable = false)
   private String emailAddress;
   
   @Column(nullable = false)
   private Boolean active = true;
   
   @OneToMany(mappedBy="person", fetch=FetchType.EAGER)
   @JsonManagedReference
   @Cascade({CascadeType.ALL})
   @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
   private Set<PersonDuty> duties;
   
   @Transient
   public void addPersonDuty(final PersonDuty pd){
      if(duties == null){
         duties = new HashSet<PersonDuty>();
      }
      
      pd.setPerson(this);
      duties.add(pd);
   }
   
}
