package com.dak.duty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lombok.NonNull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dak.duty.model.Duty;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.repository.PersonRepository;

@Service
//@Transactional
public class PersonService {

   @Autowired
   PersonRepository personRepos;

   @Autowired
   Random rand;

   public Person getPersonForDuty(@NonNull final Duty duty, final EventRoster currentEventRoster){
      return getPersonForDuty(duty, currentEventRoster, null);
   }

   public Person getPersonForDuty(@NonNull final Duty duty, final EventRoster currentEventRoster, final EventRoster priorEventRoster){
      final List<Person> people = personRepos.findByDuties_Duty(duty);//personRepos.findAll();

      if(people == null || people.size() == 0){
         return null;
      }

      final Set<Person> peopleWhoServedInLastEvent = getPeopleWhoServed(priorEventRoster);
      final Set<Person> peopleAlreadyServing = getPeopleWhoServed(currentEventRoster);

      final Map<Person, Integer> personPreferenceRanking = new HashMap<Person, Integer>();
      for(Person person : people){
         if(!CollectionUtils.isEmpty(peopleAlreadyServing) && peopleAlreadyServing.contains(person)){
            // if this person is already doing something today, make it as unlikely as possible to do something else
            personPreferenceRanking.put(person, 0);
         }
         else if(!CollectionUtils.isEmpty(peopleWhoServedInLastEvent) && peopleWhoServedInLastEvent.contains(person)){
            final Person whoDidThisDutyLastTime = priorEventRoster.getDutiesAndPeople().get(duty);
            if(whoDidThisDutyLastTime != null && whoDidThisDutyLastTime.getId() == person.getId()){
               // if this person did this same thing last time, make it as unlikely as possible to do it again
               personPreferenceRanking.put(person, 0);
            } else {
               // if this person did *something* last time (not this exact duty), make them half as likely to have to do *anythin)g* this time
               personPreferenceRanking.put(person, (int)Math.ceil(getDutyPreference(person, duty) / 2));
            }
         } else {
            personPreferenceRanking.put(person, getDutyPreference(person, duty));
         }
      }

      final ArrayList<Person> listOfPeopleTimesPreferenceRanking = new ArrayList<Person>();
      for (Map.Entry<Person, Integer> entry : personPreferenceRanking.entrySet()) {
         Person key = entry.getKey();
         Integer value = entry.getValue();
         if(value > 0){
            for(int i = 0; i < value*2; i++){
               listOfPeopleTimesPreferenceRanking.add(key);
            }
         }
      }

      // if we haven't found any candidates ...
      if(listOfPeopleTimesPreferenceRanking.size() == 0){
         // ... we'll need to include people that served last time and / or have already served today
         for (Map.Entry<Person, Integer> entry : personPreferenceRanking.entrySet()) {
            Person key = entry.getKey();
            Integer value = entry.getValue();
            if(value == 0){
               listOfPeopleTimesPreferenceRanking.add(key);
            }
         }
      }

      return CollectionUtils.isEmpty(listOfPeopleTimesPreferenceRanking) ? null : listOfPeopleTimesPreferenceRanking.get(rand.nextInt(listOfPeopleTimesPreferenceRanking.size()));
   }

   private Set<Person> getPeopleWhoServed(final EventRoster er){
      Set<Person> people = new HashSet<Person>();

      if(er != null){
         for(Person p : er.getDutiesAndPeople().values()){
            CollectionUtils.addIgnoreNull(people, p);
         }
      }

      return people;
   }

   private int getDutyPreference(@NonNull final Person p, @NonNull final Duty d){
      final Set<PersonDuty> personDuties = p.getDuties();
      for(PersonDuty pd : personDuties){
         if(pd.getDuty().getId() == d.getId()){
            return pd.getPreference();

         }
      }

      return -1;
   }
}
