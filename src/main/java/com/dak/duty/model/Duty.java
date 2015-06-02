package com.dak.duty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "duty")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Duty implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @SequenceGenerator(name = "duty_id_seq", sequenceName = "duty_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "duty_id_seq")
   @Column(nullable = false)
   private long id;
   
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
   
   public void setName(String name){
      if(name != null){
         name = name.trim();
      }
      
      this.name = name;
   }

}
