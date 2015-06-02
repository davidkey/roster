package com.dak.duty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.exception.IntervalValidationException;
import com.dak.duty.model.validation.EventTypeIntervalValidation;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "event_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@ToString
public class EventType implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "eventtype_id_seq", sequenceName = "eventtype_id_seq")
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "eventtype_id_seq")
   @Column(nullable = false)
   private long id;
   
   @Column(nullable = false, unique = true)
   @NotEmpty
   private String name;
   
   @Column(nullable = true)
   private String description;
   
   @ManyToMany(fetch=FetchType.EAGER)
   @Fetch(FetchMode.SELECT) // to prevent dupes... super annoying dupes - see https://stackoverflow.com/questions/17566304/multiple-fetches-with-eager-type-in-hibernate-with-jpa
   private List<Duty> duties;
   
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   @NotNull // see https://stackoverflow.com/questions/5982741/error-no-validator-could-be-found-for-type-java-lang-integer
   private EventTypeInterval interval;
   
   @Column(nullable = true)
   private String intervalDetail;
   
   @Temporal(TemporalType.TIME)
   @Column(nullable = false)
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="h:mm a")
   @DateTimeFormat(pattern="h:mma") // 12:15am
   private Date startTime = getBlankDate();
   
   @Temporal(TemporalType.TIME)
   @Column(nullable = false)
   @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="h:mm a")
   @DateTimeFormat(pattern="h:mma")
   private Date endTime = getBlankDate();
   
   @Transient
   private Date getBlankDate(){
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(0L));
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      
      return cal.getTime();
   }
   
   public void setInterval(final EventTypeInterval interval){
      if(intervalDetail != null && !EventTypeIntervalValidation.validate(interval, intervalDetail)){
         throw new IntervalValidationException("IntervalDetail '" + intervalDetail + "' invalid for type " + interval.toString());
      }
      
      this.interval = interval;
   }
   
   public void setIntervalDetail(final String intervalDetail){
      if(interval != null && !EventTypeIntervalValidation.validate(interval, intervalDetail)){
         throw new IntervalValidationException("IntervalDetail '" + intervalDetail + "' invalid for type " + interval.toString());
      }
      
      this.intervalDetail = intervalDetail;
   }
   
   @Transient
   public void addDuty(final Duty d){
      if(duties == null){
         duties = new ArrayList<Duty>();
      }
      
      duties.add(d);
   }
}
