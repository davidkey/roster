package com.dak.duty.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.api.util.JsonResponse;
import com.dak.duty.api.util.JsonResponse.ResponseStatus;
import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.service.PersonService;

@Controller
@RequestMapping("/api/person")
public class PersonApi {
   
   private static final Logger logger = LoggerFactory.getLogger(PersonApi.class);
   
   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   PersonService personService;
   
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
   
   @RequestMapping(method = RequestMethod.POST)
   public @ResponseBody JsonResponse save(@RequestBody Person person){
      logger.debug("person.save({})", person);
      person = personRepos.save(person);
      
      return new JsonResponse(ResponseStatus.OK, "Person saved with id " + person.getId());
   }
}
