package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dak.duty.security.CustomUserDetails;

@Entity
@Table(name = "duty")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@ToString
public class Duty implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @SequenceGenerator(name = "duty_id_seq", sequenceName = "duty_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "duty_id_seq")
   @Column(nullable = false)
   private long id;
   
   @ManyToOne
   @JoinColumn(name="org_id", nullable=false)
   private Organisation organisation;
   
   @Column(nullable = false, unique = true)
   @NotEmpty
   private String name;
   
   @Column(nullable = true)
   private String description;
   
   @Min(1)
   @Column(nullable = false)
   private Integer sortOrder = 1;
   
   @Column(nullable = false)
   private Boolean active = true;
   
   @PrePersist
   protected void onPersist() {
      if(organisation == null){ // hack?
         organisation = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson().getOrganisation();
      }
   }
   
   @PreUpdate
   protected void onUpdate() {
      if(organisation == null){ // hack?
         organisation = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson().getOrganisation();
      }
   }
   
   
   public void setName(String name){
      if(name != null){
         name = name.trim();
      }
      
      this.name = name;
   }

   /**
    * hashcode and equals exclude organisation!
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Duty other = (Duty) obj;
      if (active == null) {
         if (other.active != null)
            return false;
      } else if (!active.equals(other.active))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (sortOrder == null) {
         if (other.sortOrder != null)
            return false;
      } else if (!sortOrder.equals(other.sortOrder))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((active == null) ? 0 : active.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((sortOrder == null) ? 0 : sortOrder.hashCode());
      return result;
   }

}
