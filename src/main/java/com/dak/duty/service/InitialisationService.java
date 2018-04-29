package com.dak.duty.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dak.duty.exception.InvalidPasswordException;
import com.dak.duty.form.SetupForm;
import com.dak.duty.model.Duty;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventType;
import com.dak.duty.model.Organisation;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonRole;
import com.dak.duty.model.enums.EventTypeInterval;
import com.dak.duty.model.enums.IntervalWeekly;
import com.dak.duty.model.enums.Role;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.EventTypeRepository;
import com.dak.duty.repository.OrganisationRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;

import lombok.NonNull;

@Service
@Transactional
public class InitialisationService {

	private static final Logger logger = LoggerFactory.getLogger(InitialisationService.class);
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private EventTypeRepository eventTypeRepos;

	@Autowired
	private DutyRepository dutyRepos;

	@Autowired
	private PersonRepository personRepos;

	@Autowired
	private EventRepository eventRepos;

	@Autowired
	private IntervalService intervalService;

	@Autowired
	private PersonService personService;

	@Autowired
	private OrganisationRepository orgRepos;

	protected void clearAllData() {
		logger.info("clearAllData");

		this.eventRepos.deleteAll();
		this.eventRepos.flush();

		this.personRepos.deleteAll();
		this.personRepos.flush();

		this.eventTypeRepos.deleteAll();
		this.eventTypeRepos.flush();

		this.dutyRepos.deleteAll();
		this.dutyRepos.flush();
	}

	public boolean initSetupComplete() {
		return !this.personRepos
				.findAll(Specifications.where(PersonSpecs.isActive()).and(PersonSpecs.sameOrg()).and(PersonSpecs.hasRole(Role.ROLE_ADMIN)))
				.isEmpty();
	}

	public void populateDefaultData() {
		logger.info("populateDefaultData");
		this.clearAllData();

		final List<Organisation> defaultOrgs = this.getDefaultOrganisations();
		logger.debug("defaultOrganisations: {}", defaultOrgs);
		this.orgRepos.save(defaultOrgs);

		final List<Duty> defaultDuties = this.getDefaultDuties(defaultOrgs);
		logger.debug("defaultDuties: {}", defaultDuties);
		this.dutyRepos.save(defaultDuties);

		final List<EventType> defaultEventTypes = this.getDefaultEventTypes(defaultDuties, defaultOrgs);
		logger.debug("defaultEventTypes: {}", defaultEventTypes);
		this.eventTypeRepos.save(defaultEventTypes);

		final List<Person> defaultPeople = this.getDefaultPeople(defaultDuties, defaultOrgs);
		logger.debug("defaultPeople: {}", defaultPeople);
		this.personService.save(defaultPeople);

		final List<Event> defaultEvents = this.getDefaultEvents(defaultEventTypes, defaultOrgs);
		logger.debug("defaultEvents: {}", defaultEvents);
		this.eventRepos.save(defaultEvents);

		this.createDefaultAdminUser(this.getDefaultAdminUser());

	}

	protected List<Organisation> getDefaultOrganisations() {
		final List<Organisation> orgs = new ArrayList<>();
		orgs.add(this.getDefaultOrg());

		return orgs;
	}

	public void createOrganisationAndAdminUser(final SetupForm setupForm) {
		final Organisation org = this.createOrganisation(setupForm.getOrganisationName().trim());
		this.createDefaultAdminUser(setupForm.getEmailAddress().trim(), setupForm.getPassword(), setupForm.getNameLast().trim(),
				setupForm.getNameFirst().trim(), org);
	}

	public Organisation createOrganisation(@NonNull final String name) {
		final Organisation org = new Organisation();
		org.setName(name.trim());
		org.setRegistrationCode(getRegistrationCode(org.getName()));

		return this.orgRepos.save(org);
	}
	
	private String getRegistrationCode(final String orgName) {

		String nameSquashed = orgName.trim().toUpperCase().replace(" ", "");
		
		if (nameSquashed.length() > 5) {
			nameSquashed = nameSquashed.substring(0, 5);
		}
		
		final Long orgNumber = orgRepos.countByRegistrationCodeStartsWith(nameSquashed);
		
		return nameSquashed + String.format("%05d", orgNumber + 1);
	}

	public void createDefaultAdminUser(final Person defaultAdminUser) {
		logger.info("createDefaultAdminUser({})", defaultAdminUser);

		this.createDefaultAdminUser(defaultAdminUser.getEmailAddress(), defaultAdminUser.getPassword(), defaultAdminUser.getNameLast(),
				defaultAdminUser.getNameFirst(), defaultAdminUser.getOrganisation());
	}

	public void createDefaultAdminUser(final String email, final String password, final String lastName, final String firstName,
			final Organisation org) {
		logger.info("createDefaultAdminUser({})", email);

		if (!this.personService.isPasswordValid(password)) {
			throw new InvalidPasswordException(this.personService.getPasswordRequirements());
		}

		final Person person = new Person();
		person.setEmailAddress(email);
		person.setPassword(this.encoder.encode(password));
		person.setNameFirst(firstName);
		person.setNameLast(lastName);
		person.setActive(true);
		person.setOrganisation(org);

		final PersonRole adminRole = new PersonRole();
		adminRole.setRole(Role.ROLE_ADMIN);

		final PersonRole userRole = new PersonRole();
		userRole.setRole(Role.ROLE_USER);

		person.addRole(adminRole);
		person.addRole(userRole);

		this.personService.save(person, true);
	}

	public Organisation getDefaultOrg() {
		final Organisation org = new Organisation();
		org.setId(1L);
		org.setName("My First Org");
		org.setRegistrationCode("MYFIRST001");
		return org;
	}

	public Person getDefaultAdminUser() {
		final Person person = new Person();
		person.setEmailAddress("davidkey@gmail.com");
		person.setPassword(this.encoder.encode("password"));
		person.setNameFirst("David");
		person.setNameLast("Key");
		person.setActive(true);
		person.setOrganisation(this.getDefaultOrg());

		final PersonRole adminRole = new PersonRole();
		adminRole.setRole(Role.ROLE_ADMIN);

		final PersonRole userRole = new PersonRole();
		userRole.setRole(Role.ROLE_USER);

		person.addRole(adminRole);
		person.addRole(userRole);

		return person;
	}

	public void createDefaultAdminUser(final String email, final String password, final Organisation org) {
		this.createDefaultAdminUser(email, password, "USER", "ADMIN", org);
	}

	protected List<Event> getDefaultEvents(final List<EventType> eventTypes, final List<Organisation> orgs) {
		final List<Event> events = new ArrayList<>(eventTypes.size());

		for (final EventType et : eventTypes) {
			final Event e = new Event();

			e.setName(et.getName());
			try {
				if (e.getName().contains("Sunday")) {
					e.setDateEvent(InitialisationService.fmt.parseDateTime("05/24/2015").toDate()); // sunday
				} else {
					e.setDateEvent(InitialisationService.fmt.parseDateTime("05/27/2015").toDate()); // wednesday
				}
			} catch (final IllegalArgumentException iae) {
				// do nothing
			}

			e.setEventType(et);
			e.setOrganisation(orgs.get(0));

			events.add(e);
		}

		return events;
	}

	protected List<Person> getDefaultPeople(final List<Duty> duties, final List<Organisation> orgs) {
		final List<Person> people = new ArrayList<>();
		final DataFactory df = new DataFactory();
		final RandomDataGenerator randomData = new RandomDataGenerator();

		final List<Duty> scrambledDuties = new ArrayList<>(duties);
		for (int i = 0; i < 25; i++) {
			final Person p = new Person();
			p.setActive(true);
			p.setEmailAddress(df.getEmailAddress());
			p.setNameFirst(df.getFirstName());
			p.setNameLast(df.getLastName());
			p.setOrganisation(orgs.get(0));

			final PersonRole personRole = new PersonRole();
			personRole.setRole(Role.ROLE_USER);

			p.addRole(personRole);

			Collections.shuffle(scrambledDuties);
			final int numDuties = randomData.nextInt(2, scrambledDuties.size() - 1);
			for (int x = 0; x < numDuties; x++) {
				p.addDutyAndPreference(scrambledDuties.get(x), randomData.nextInt(1, 9));
			}

			people.add(p);
		}

		return people;
	}

	protected List<Duty> getDefaultDuties(final List<Organisation> orgs) {
		final List<Duty> duties = new ArrayList<>();

		Duty duty = null;
		int count = 1;

		duty = new Duty();
		duty.setName("Song Leading");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Opening Prayer");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Closing Prayer");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Announcements");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Scripture Reading");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Preaching");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Table");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		duty = new Duty();
		duty.setName("Invitation");
		duty.setDescription(duty.getName());
		duty.setSortOrder(count++);
		duty.setOrganisation(orgs.get(0));
		duties.add(duty);

		return duties;
	}

	protected List<EventType> getDefaultEventTypes(@NonNull final List<Duty> duties, @NonNull final List<Organisation> orgs) {
		final List<Duty> sundayAmDuties = new ArrayList<>();
		final List<Duty> sundayPmDuties = new ArrayList<>();
		final List<Duty> wednesdayDuties = new ArrayList<>();

		for (final Duty d : duties) {
			switch (d.getName()) {
			case "Song Leading":
			case "Opening Prayer":
			case "Closing Prayer":
			case "Announcements":
				sundayAmDuties.add(d);
				sundayPmDuties.add(d);
				wednesdayDuties.add(d);
				break;
			case "Scripture Reading":
			case "Preaching":
				sundayAmDuties.add(d);
				sundayPmDuties.add(d);
				break;
			case "Table":
				sundayAmDuties.add(d);
				sundayAmDuties.add(d);
				sundayAmDuties.add(d);
				sundayAmDuties.add(d);
				sundayPmDuties.add(d);
				break;
			case "Invitation":
				wednesdayDuties.add(d);
				break;
			default:
				logger.warn("possible problem - we've got a default duty ({}) without an associated event.", d);
				break;
			}
		}

		final EventType sundayAm = new EventType();
		sundayAm.setName("Sunday AM");
		sundayAm.setDescription(sundayAm.getName());
		sundayAm.setDuties(sundayAmDuties);
		sundayAm.setInterval(EventTypeInterval.WEEKLY);
		sundayAm.setIntervalDetail(IntervalWeekly.SUNDAY.toString());
		sundayAm.setStartTime(this.intervalService.getTimeWithoutDate(9, 30));
		sundayAm.setEndTime(this.intervalService.getTimeWithoutDate(11, 30));
		sundayAm.setOrganisation(orgs.get(0));

		final EventType sundayPm = new EventType();
		sundayPm.setName("Sunday PM");
		sundayPm.setDescription(sundayPm.getName());
		sundayPm.setDuties(sundayPmDuties);
		sundayPm.setInterval(EventTypeInterval.WEEKLY);
		sundayPm.setIntervalDetail(IntervalWeekly.SUNDAY.toString());
		sundayPm.setStartTime(this.intervalService.getTimeWithoutDate(18, 30));
		sundayPm.setEndTime(this.intervalService.getTimeWithoutDate(20, 0));
		sundayPm.setOrganisation(orgs.get(0));

		final EventType wednesday = new EventType();
		wednesday.setName("Wednesday PM");
		wednesday.setDescription(wednesday.getName());
		wednesday.setDuties(wednesdayDuties);
		wednesday.setInterval(EventTypeInterval.WEEKLY);
		wednesday.setIntervalDetail(IntervalWeekly.WEDNESDAY.toString());
		wednesday.setStartTime(this.intervalService.getTimeWithoutDate(19, 30));
		wednesday.setEndTime(this.intervalService.getTimeWithoutDate(21, 0));
		wednesday.setOrganisation(orgs.get(0));

		final List<EventType> eventTypes = new ArrayList<>();
		eventTypes.add(sundayAm);
		eventTypes.add(sundayPm);
		eventTypes.add(wednesday);

		return eventTypes;
	}
}
