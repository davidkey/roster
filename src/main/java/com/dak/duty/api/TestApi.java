package com.dak.duty.api;

import static com.dak.duty.repository.specification.PersonSpecs.isActive;
import static com.dak.duty.repository.specification.PersonSpecs.sameOrg;
import static com.dak.duty.repository.specification.PersonSpecs.hasDuty;
import static com.dak.duty.repository.specification.PersonSpecs.nameFirstLike;
import static com.dak.duty.repository.specification.PersonSpecs.orderByNameLastAscNameFirstAsc;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dak.duty.model.Duty;
import com.dak.duty.model.Person;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.PersonRepository;

@Controller
@RequestMapping("/test")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class TestApi {

   @Autowired
   PersonRepository personRepos;
   
   @Autowired
   DutyRepository dutyRepos;
   
   @RequestMapping("/1")
   public @ResponseBody List<Person> getSomething(){
      Duty d = dutyRepos.findAll().get(0);
      return personRepos.findAll(where(isActive()).and(sameOrg()).and(hasDuty(d)));
   }
   
   @RequestMapping("/2")
   public @ResponseBody List<Person> getSomething2(){
      Duty d = dutyRepos.findAll().get(0);
      return personRepos.findAll(where(isActive()).and(sameOrg()).and(hasDuty(d)).and(orderByNameLastAscNameFirstAsc()));
   }
   
   
   @RequestMapping("/3")
   public @ResponseBody List<Person> getSomething3(){
      return personRepos.findAll(where(isActive()).and(sameOrg()).and(nameFirstLike("MEN")).and(orderByNameLastAscNameFirstAsc()));
   }
}
