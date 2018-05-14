package com.dak.duty.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.mocking.WithMockCustomUserAdmin;

@RunWith(SpringJUnit4ClassRunner.class)
@WithMockCustomUserAdmin
public class PersonServiceTest extends ServiceTest {
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private PersonRepository personRepository;

	public PersonServiceTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testPersonService() {
		personService.getUpcomingDuties(personRepository.getOne(1L));
	}
}
