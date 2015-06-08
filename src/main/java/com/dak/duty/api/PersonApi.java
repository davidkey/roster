package com.dak.duty.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.DutyNode;
import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRosterItem;
import com.dak.duty.model.Person;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/api/person")
@PreAuthorize("hasRole('ROLE_USER')")
public class PersonApi {

   private static final Logger logger = LoggerFactory.getLogger(PersonApi.class);

   @Autowired
   PersonRepository personRepos;

   @Autowired
   PersonService personService;

   @Autowired
   EventRepository eventRepos;

   @Autowired
   BCryptPasswordEncoder encoder;

   @PreAuthorize("hasRole('ROLE_ADMIN')")
   @RequestMapping(method = RequestMethod.DELETE)
   public @ResponseBody JsonResponse delete(@RequestBody Person person){
      logger.debug("person.delete({})", person);

      person = personRepos.findOne(person.getId());
      person.setActive(false);
      personRepos.save(person);

      return new JsonResponse(ResponseStatus.OK, "Person " + person.getId() + " deleted");
   }

   @RequestMapping(value="/{id}", method = RequestMethod.GET)
   public @ResponseBody Person get(@PathVariable("id") Long id){
      logger.debug("person.get({})", id);

      return personRepos.findOne(id);
   }

   @RequestMapping(value="/{id}/upcomingDuties", method = RequestMethod.GET)
   @PreAuthorize("#p.emailAddress == authentication.name or hasRole('ROLE_ADMIN')")
   public @ResponseBody List<DutyNode> getUpcomingDuties(@PathVariable("id") @P("p") Person person){
      List<DutyNode> myDuties = new ArrayList<DutyNode>();

      List<Event> eventsWithPerson = eventRepos.findAllByRoster_Person(person);

      for(Event event : eventsWithPerson){
         Set<EventRosterItem> roster = event.getRoster();
         for(EventRosterItem eri : roster){
            if(eri.getPerson().getId() == person.getId()){
               myDuties.add(new DutyNode(event.getName(), event.getDateEvent(), eri.getDuty().getName()));
            }
         }
      }

      return myDuties;
   }


   @PreAuthorize("hasRole('ROLE_ADMIN')")
   @RequestMapping(method = RequestMethod.POST)
   public @ResponseBody JsonResponse save(@RequestBody Person person){
      logger.debug("person.save({})", person);
      person = personRepos.save(person);

      return new JsonResponse(ResponseStatus.OK, "Person saved with id " + person.getId());
   }

   @PreAuthorize("hasRole('ROLE_ADMIN')")
   @RequestMapping(value = "/password", method = RequestMethod.POST)
   public @ResponseBody JsonResponse setPassword(@RequestBody Person person){
      logger.debug("setPassword.save({})", person.getId());

      Person personToUpdate = personRepos.findOne(person.getId());
      if(personToUpdate != null){
         personToUpdate.setPassword(encoder.encode(person.getPassword()));
         personToUpdate = personRepos.save(personToUpdate);
         return new JsonResponse(ResponseStatus.OK, "Password updated for Person " + personToUpdate.getId());
      } else {
         return new JsonResponse(ResponseStatus.ERROR, "Person not found - id " + person.getId());
      }
   }
}
