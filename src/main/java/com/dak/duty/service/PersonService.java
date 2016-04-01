package com.dak.duty.service;

import static com.dak.duty.repository.specification.PersonSpecs.hasDuty;
import static com.dak.duty.repository.specification.PersonSpecs.isActive;
import static com.dak.duty.repository.specification.PersonSpecs.sameOrg;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import lombok.NonNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.MultiValueMap;

import com.dak.duty.api.util.DutyNode;
import com.dak.duty.exception.InvalidIdException;
import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.exception.UsernameAlreadyExists;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Email;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.model.PersonRole;
import com.dak.duty.model.enums.Role;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.facade.IPasswordResetTokenFacade;

@Service
//@Transactional
public class PersonService {

   private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

   @Autowired
   PersonRepository personRepos;

   @Autowired
   EventRepository eventRepos;

   @Autowired
   DutyRepository dutyRepos;

   @Autowired
   IntervalService intervalService;

   @Autowired
   IAuthenticationFacade authenticationFacade;

   @Autowired
   Random rand;

   @Autowired
   BCryptPasswordEncoder encoder;

   @Autowired
   @Qualifier("authenticationManagerBean")
   AuthenticationManager authenticationManager;

   @Autowired
   IPasswordResetTokenFacade passwordResetTokenGenerator;

   @Autowired
   EmailService<MailgunMailMessage> emailService;

   @Autowired
   private VelocityEngine velocityEngine;

   public List<PersonRole> getDefaultRoles(){
      final List<PersonRole> personRoles = new ArrayList<PersonRole>();
      PersonRole userRole = new PersonRole();
      userRole.setRole(Role.ROLE_USER);
      personRoles.add(userRole);

      return personRoles;
   }

   public void initiatePasswordReset(final String emailAddress, final String resetBaseUrl){
      Person person = personRepos.findByEmailAddress(emailAddress);

      if(person == null){
         throw new InvalidIdException("person with that email address not found");
      }

      initiatePasswordReset(person, resetBaseUrl);
   }

   @SuppressWarnings("unchecked")
   @Transactional
   public void initiatePasswordReset(final Person person, final String resetBaseUrl){
      final int EXPIRE_MIN = 90;
      final String resetToken = passwordResetTokenGenerator.getNextPasswordResetToken();

      person.setResetToken(resetToken);
      person.setResetTokenExpires(getMinutesInFuture(new Date(), EXPIRE_MIN));

      personRepos.save(person);

      @SuppressWarnings("rawtypes")
      Map model = new HashMap();                 
      model.put("name", person.getNameFirst() + " " + person.getNameLast());
      model.put("email", person.getEmailAddress());
      model.put("resetAddress", resetBaseUrl + resetToken);
      model.put("expireMinutes", EXPIRE_MIN);
      model.put("imageUrl", "https://roster.guru/resources/images/rosterGuruEmailHeader.png");

      emailService.send(
            new Email(
                  "admin@roster.guru", 
                  person.getEmailAddress(), 
                  "Password Reset Initiated", 
                  VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/passwordReset.vm", "UTF-8", model)
                  ));

   }


   private Date getMinutesInFuture(final Date d, final int minutes){
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(d);
      calendar.add(Calendar.MINUTE, minutes);
      return calendar.getTime();
   }

   public boolean loginAsPerson(final String username, final String password, final HttpServletRequest request){
      logger.debug("loginAsPerson({}, {})", username, password);
      try {
         // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
         UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
         token.setDetails(new WebAuthenticationDetails(request));
         Authentication authentication = authenticationManager.authenticate(token);
         logger.debug("Logging in with [{}]", authentication.getPrincipal());
         SecurityContextHolder.getContext().setAuthentication(authentication);
         return true;
      } catch (Exception e) {
         SecurityContextHolder.getContext().setAuthentication(null);
         logger.error("Failure in autoLogin", e);
         return false;
      }
   }

   @Transactional
   public Person clearResetToken(final Person person){
      person.setResetToken(null);
      person.setResetTokenExpires(null);

      return personRepos.save(person);
   }

   @Transactional
   public Person setPassword(Person person, final String plaintextPassword){
      person.setPassword(encoder.encode(plaintextPassword));
      person = personRepos.save(person);
      personRepos.flush();
      return person;
   }
   
   public Boolean isCurrentPassword(final Person person, final String password){
      return encoder.matches(password, getCurrentEncodedPassword(person));
   }
   
   private String getCurrentEncodedPassword(@NonNull Person person){
      person = personRepos.findOne(person.getId());
      
      if(person == null){
         throw new InvalidIdException("person not found");
      }
      
      return person.getPassword();
   }

   public boolean isPasswordValid(final String password){
      return password != null && password.length() >= 6;
   }

   public String getPasswordRequirements(){
      return "Password must be at least 6 digits";
   }

   public List<DutyNode> getUpcomingDuties (final Person person){
      List<DutyNode> myDuties = new ArrayList<DutyNode>();

      List<Event> eventsWithPerson = eventRepos.findAllByRoster_PersonAndDateEventGreaterThanEqualOrderByDateEventAsc(person, intervalService.getCurrentSystemDate());

      for(Event event : eventsWithPerson){
         Set<EventRosterItem> roster = event.getRoster();
         for(EventRosterItem eri : roster){
            if(eri.getPerson().getId() == person.getId()){
               myDuties.add(new DutyNode(event.getName(), event.getDateEvent(), eri.getDuty().getName(), eri.getDuty().getId(), eri.getEvent().getId()));
            }
         }
      }

      return myDuties;
   }

   @Transactional
   public Iterable<Person> save(Iterable<Person> people){
      if(people != null){
         for(Person person : people){
            // business logic to prevent duplicate email addresses
            // we couldn't do this in a unique index because we allow nulls
            if(person.getEmailAddress() != null && person.getEmailAddress().length() > 0){
               Person personWithThisEmailAddress = personRepos.findByEmailAddress(person.getEmailAddress());

               if(personWithThisEmailAddress != null && personWithThisEmailAddress.getId() != person.getId()){
                  throw new UsernameAlreadyExists(personWithThisEmailAddress.getEmailAddress());
               }
            }
         }
      }

      return personRepos.save(people);
   }

   @Transactional
   public Person save(Person person, Boolean force){
      // business logic to prevent duplicate email addresses
      // we couldn't do this in a unique index because we allow nulls
      if(person.getEmailAddress() != null && person.getEmailAddress().length() > 0){
         Person personWithThisEmailAddress = personRepos.findByEmailAddress(person.getEmailAddress());

         if(personWithThisEmailAddress != null && personWithThisEmailAddress.getId() != person.getId()){
            throw new UsernameAlreadyExists(personWithThisEmailAddress.getEmailAddress());
         }
      }

      if(!force && person.getOrganisation() != null && !person.getOrganisation().getId().equals(authenticationFacade.getOrganisation().getId())){
         throw new RosterSecurityException("can't do that");
      }

      return personRepos.save(person);
   }

   @Transactional
   public Person save(Person person){
      return save(person, false);
   }

   public Person getPersonForDuty(@NonNull final Duty duty, final EventRoster currentEventRoster){
      return getPersonForDuty(duty, currentEventRoster, new HashSet<Person>());
   }

   public Person getPersonForDuty(@NonNull final Duty duty, final EventRoster currentEventRoster, @NonNull final Set<Person> peopleExcluded){
      Set<Long> ids = new HashSet<Long>();

      if(peopleExcluded != null){
         for(Person p : peopleExcluded){
            ids.add(p.getId());
         }
      }

      return getPersonForDutyExcludedById(duty, currentEventRoster, ids);
   }

   public Person getPersonForDutyExcludedById(@NonNull final Duty duty, final EventRoster currentEventRoster, @NonNull final Set<Long> peopleIdsExcluded){
      final List<Person> people =  personRepos.findAll(where(isActive()).and(sameOrg()).and(hasDuty(duty)));
      if(CollectionUtils.isEmpty(people)){
         return null;
      }

      final Set<Person> peopleAlreadyServing = getPeopleWhoServed(currentEventRoster);

      final Map<Person, Integer> personPreferenceRanking = new HashMap<Person, Integer>();
      for(Person person : people){
         if(peopleIdsExcluded != null && peopleIdsExcluded.contains(person.getId())){
            personPreferenceRanking.put(person, -1);
         } else if(!CollectionUtils.isEmpty(peopleAlreadyServing) && peopleAlreadyServing.contains(person)){
            if(getPeopleServingDuty(duty, currentEventRoster).contains(person)){  
               // if this person is already doing THIS exact duty today, don't let them do it again!
               personPreferenceRanking.put(person, -1);
            } else { 
               // if this person is already doing something today, make it as unlikely as possible to do something else
               personPreferenceRanking.put(person, 0);
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
      if(listOfPeopleTimesPreferenceRanking.isEmpty()){
         // ... we'll need to include people that served last time and / or have already served today
         for (Map.Entry<Person, Integer> entry : personPreferenceRanking.entrySet()) {
            Person key = entry.getKey();
            Integer value = entry.getValue();
            if(value.equals(0)){
               listOfPeopleTimesPreferenceRanking.add(key);
            }
         }
      }

      return CollectionUtils.isEmpty(listOfPeopleTimesPreferenceRanking) ? null : listOfPeopleTimesPreferenceRanking.get(rand.nextInt(listOfPeopleTimesPreferenceRanking.size()));
   }

   private Set<Person> getPeopleServingDuty(@NonNull final Duty duty, @NonNull final EventRoster currentEventRoster){
      Set<Person> peopleServingDuty = new HashSet<Person>();

      for(Entry<Duty, Person> entry : currentEventRoster.getDutiesAndPeople()){
         if(entry.getKey() == null || entry.getValue() == null){
            continue;
         }

         if(entry.getKey().getId() == duty.getId()){
            peopleServingDuty.add(entry.getValue());
         }
      }

      return peopleServingDuty;
   }


   private Set<Person> getPeopleWhoServed(final EventRoster er){
      Set<Person> people = new HashSet<Person>();

      if(er != null){
         for(int i = 0; i < er.getDutiesAndPeople().size(); i++){
            CollectionUtils.addIgnoreNull(people, er.getDutiesAndPeople().get(i).getValue());
         }
      }

      return people;
   }

   private int getDutyPreference(@NonNull final Person p, @NonNull final Duty d){
      final Set<PersonDuty> personDuties = p.getDuties();
      for(PersonDuty pd : personDuties){
         if(pd.getDuty().getId() == d.getId()){
            return pd.getWeightedPreference();

         }
      }

      return -1;
   }

   @Transactional
   public void updateDutiesFromFormPost(@NonNull Person person, @NonNull final MultiValueMap<String, String> parameters){
      logger.debug("updateDutesForPersonFromFormPost({})", person.getId());

      final Map<Long, Integer> dutyPrefs = new HashMap<Long, Integer>();

      for (final Iterator<Entry<String, List<String>>> iter = parameters.entrySet().iterator(); iter.hasNext();) {
         final Entry<String, List<String>> entry = iter.next();
         final String key = entry.getKey();
         final List<String> vals = entry.getValue();

         if(key != null && vals != null && vals.size() > 0 && key.startsWith("duty_")){
            final long dutyId = Long.parseLong(key.split("_")[1]);
            final int prefRanking = Integer.parseInt(vals.get(0));
            dutyPrefs.put(dutyId, prefRanking);
         }
      }

      logger.info("Duty Prefs: {}", dutyPrefs);

      boolean personUpdated = false;
      for (Map.Entry<Long, Integer> entry : dutyPrefs.entrySet()) { 
         final Long dutyId = entry.getKey();

         if(dutyId != null && dutyId >= 0){
            final Duty duty = dutyRepos.findOne(dutyId);
            final int prefRanking = entry.getValue();

            person.addOrUpdateDutyAndPreference(duty, prefRanking);
            personUpdated = true;
         }
      }

      if(personUpdated){
         this.save(person);
      }
   }

}
