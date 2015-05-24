package com.dak.duty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.model.Person;
import com.dak.duty.repository.PersonRepository;

@Controller
@RequestMapping("/person")
public class PersonController {

   @Autowired
   PersonRepository personRepos;
   
   @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
   @ResponseBody Person getPersonById(@PathVariable Long personId){
      return personRepos.findOne(personId);
   }
}
