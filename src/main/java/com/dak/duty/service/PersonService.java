package com.dak.duty.service;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.dak.duty.api.util.DutyNode;
import com.dak.duty.exception.InvalidIdException;
import com.dak.duty.exception.RosterSecurityException;
import com.dak.duty.exception.UsernameAlreadyExists;
import com.dak.duty.model.AuthRequest;
import com.dak.duty.model.Duty;
import com.dak.duty.model.DutyPreference;
import com.dak.duty.model.Email;
import com.dak.duty.model.Event;
import com.dak.duty.model.EventRoster;
import com.dak.duty.model.MailgunMailMessage;
import com.dak.duty.model.Person;
import com.dak.duty.model.PersonDuty;
import com.dak.duty.model.PersonRole;
import com.dak.duty.model.enums.Role;
import com.dak.duty.repository.DutyRepository;
import com.dak.duty.repository.EventRepository;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.repository.specification.PersonSpecs;
import com.dak.duty.security.CustomUserDetails;
import com.dak.duty.security.IAuthenticationFacade;
import com.dak.duty.service.facade.IPasswordResetTokenFacade;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {

	private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
	
	@Value("${jwt.signingKey}")
	private String signingKey;
	
	private static final Integer DUTY_CHANCE_NEVER = -1;
	private static final Integer DUTY_CHANCE_LOWEST = 0;
	
	private static final Integer EXPIRE_MIN = 90;

	private final PersonRepository personRepos;
	private final EventRepository eventRepos;
	private final DutyRepository dutyRepos;
	private final IntervalService intervalService;
	private final IAuthenticationFacade authenticationFacade;
	private final PasswordEncoder encoder;
	private final AuthenticationManager authenticationManager;
	private final IPasswordResetTokenFacade passwordResetTokenGenerator;
	private final EmailService<MailgunMailMessage> emailService;
	private final VelocityEngine velocityEngine;

	public List<PersonRole> getDefaultRoles() {
		final List<PersonRole> personRoles = new ArrayList<>();
		final PersonRole userRole = new PersonRole();
		userRole.setRole(Role.ROLE_USER);
		personRoles.add(userRole);

		return personRoles;
	}

	public void initiatePasswordReset(final String emailAddress, final String resetBaseUrl) {
		final Person person = this.personRepos.findByEmailAddress(emailAddress);

		if (person == null) {
			throw new InvalidIdException("person with that email address not found");
		}

		this.initiatePasswordReset(person, resetBaseUrl);
	}

	@Transactional
	public void initiatePasswordReset(final Person person, final String resetBaseUrl) {
		final String resetToken = this.passwordResetTokenGenerator.getNextPasswordResetToken();

		person.setResetToken(resetToken);
		person.setResetTokenExpires(LocalDateTime.now().plusMinutes(EXPIRE_MIN));

		this.personRepos.save(person);

		VelocityContext context = new VelocityContext();
		context.put("name", person.getNameFirst() + " " + person.getNameLast());
		context.put("email", person.getEmailAddress());
		context.put("resetAddress", resetBaseUrl + resetToken);
		context.put("expireMinutes", EXPIRE_MIN);
		context.put("imageUrl", "https://roster.guru/resources/images/rosterGuruEmailHeader.png");

		StringWriter stringWriter = new StringWriter();
		velocityEngine.mergeTemplate("velocity/passwordReset.vm", "UTF-8", context, stringWriter);

		this.emailService.send(new Email("admin@roster.guru", person.getEmailAddress(), "Password Reset Initiated", stringWriter.toString()));
	}
	
	public Authentication attemptAuthentication(final String username, final String password) {
		return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	
	public String getJwtForUser(final AuthRequest authRequest) {
		final Authentication auth;
		try {
			auth = this.attemptAuthentication(authRequest.getUsername(), authRequest.getPassword());
		} catch (AuthenticationException ae) {
			logger.debug("auth failed for user {}", authRequest.getUsername(), ae);
			throw new RuntimeException("auth failed for user " + authRequest.getUsername());
		}

		Map<String, Object> additionalClaims = new HashMap<>();

		final CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
		
		additionalClaims.put("roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
		additionalClaims.put("fullName", user.getPerson().getNameFirst() + " " + user.getPerson().getNameLast());
		additionalClaims.put("orgId", user.getPerson().getOrganisation().getId());
		additionalClaims.put("orgName", user.getPerson().getOrganisation().getName());

		JwtBuilder builder = Jwts.builder().setId(authRequest.getUsername())
				.setIssuedAt(new Date())
				.setSubject("roster")
				.setIssuer("roster.guru")
				.addClaims(additionalClaims)
				.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 60 * 12)) // 12 hours
				//.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 6)) // 6 minutes
				.signWith(SignatureAlgorithm.HS256, signingKey.getBytes(Charset.forName("UTF-8")));

		final String jwt = builder.compact();

		final HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

		return jwt;
	}

	public boolean loginAsPerson(final String username, final String password, final HttpServletRequest request) {
		logger.debug("loginAsPerson({}, ********)", username);
		try {
			// Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			token.setDetails(new WebAuthenticationDetails(request));
			final Authentication authentication = this.authenticationManager.authenticate(token);
			logger.debug("Logging in with [{}]", authentication.getPrincipal());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return true;
		} catch (final Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			logger.error("Failure in autoLogin", e);
			return false;
		}
	}

	@Transactional
	public Person clearResetToken(final Person person) {
		person.setResetToken(null);
		person.setResetTokenExpires(null);

		return this.personRepos.save(person);
	}

	@Transactional
	public Person setPassword(Person person, final String plaintextPassword) {
		person.setPassword(this.encoder.encode(plaintextPassword));
		person = this.personRepos.save(person);
		this.personRepos.flush();
		return person;
	}

	public Boolean isCurrentPassword(final Person person, final String password) {
		return this.encoder.matches(password, this.getCurrentEncodedPassword(person));
	}

	private String getCurrentEncodedPassword(@NonNull Person person) {
		person = this.personRepos.findOne(person.getId());

		if (person == null) {
			throw new InvalidIdException("person not found");
		}

		return person.getPassword();
	}

	public boolean isPasswordValid(final String password) {
		return password != null && password.length() >= 6;
	}

	public String getPasswordRequirements() {
		return "Password must be at least 6 digits";
	}
	
	public List<DutyNode> getUpcomingDuties(final Person person) {
		return this.eventRepos.findAllByRoster_PersonAndDateEventGreaterThanEqualOrderByDateEventAsc(person, this.intervalService.getCurrentSystemDate()).stream()
			.map(Event::getRoster)
			.flatMap(Collection::stream)
			.filter(eri -> eri.getPerson().getId() == person.getId())
			.map(eri -> new DutyNode(eri.getEvent().getName(), eri.getEvent().getDateEvent(), eri.getDuty().getName(), eri.getDuty().getId(), eri.getEvent().getId()))
			.collect(Collectors.toList());
	}

	@Transactional
	public Iterable<Person> save(final Iterable<Person> people) {
		if (people != null) {
			for (final Person person : people) {
				// business logic to prevent duplicate email addresses
				// we couldn't do this in a unique index because we allow nulls
				if (person.getEmailAddress() != null && person.getEmailAddress().length() > 0) {
					final Person personWithThisEmailAddress = this.personRepos.findByEmailAddress(person.getEmailAddress());

					if (personWithThisEmailAddress != null && personWithThisEmailAddress.getId() != person.getId()) {
						throw new UsernameAlreadyExists(personWithThisEmailAddress.getEmailAddress());
					}
				}
			}
		}

		return this.personRepos.saveAll(people);
	}

	@Transactional
	public Person save(final Person person, final Boolean force) {
		// business logic to prevent duplicate email addresses
		// we couldn't do this in a unique index because we allow nulls
		if (person.getEmailAddress() != null && person.getEmailAddress().length() > 0) {
			final Person personWithThisEmailAddress = this.personRepos.findByEmailAddress(person.getEmailAddress());

			if (personWithThisEmailAddress != null && personWithThisEmailAddress.getId() != person.getId()) {
				throw new UsernameAlreadyExists(personWithThisEmailAddress.getEmailAddress());
			}
		}

		if (!force && person.getOrganisation() != null
				&& !person.getOrganisation().getId().equals(this.authenticationFacade.getOrganisation().getId())) {
			throw new RosterSecurityException("can't do that");
		}

		return this.personRepos.save(person);
	}

	@Transactional
	public Person save(final Person person) {
		return this.save(person, false);
	}

	public Optional<Person> getPersonForDuty(@NonNull final Duty duty, final EventRoster currentEventRoster) {
		return this.getPersonForDuty(duty, currentEventRoster, Collections.emptySet());
	}

	public Optional<Person> getPersonForDuty(@NonNull final Duty duty, final EventRoster currentEventRoster, @NonNull final Set<Person> peopleExcluded) {
		return this.getPersonForDutyExcludedById(duty, currentEventRoster, peopleExcluded.stream().map(Person::getId).collect(Collectors.toSet()));
	}
	
	public Optional<Person> getPersonForDutyExcludedById(@NonNull final Duty duty, final EventRoster currentEventRoster, @NonNull final Set<Long> peopleIdsExcluded) {
		final List<Person> people = this.personRepos
				.findAll(PersonSpecs.isActive().and(PersonSpecs.sameOrg()).and(PersonSpecs.hasDuty(duty)));
		if (CollectionUtils.isEmpty(people)) {
			return Optional.empty();
		}

		final Set<Person> peopleAlreadyServing = this.getPeopleWhoServed(currentEventRoster);

		final Map<Person, Integer> personPreferenceRanking = new HashMap<>();
		for (final Person person : people) {
			if (peopleIdsExcluded.contains(person.getId())) {
				personPreferenceRanking.put(person, DUTY_CHANCE_NEVER);
			} else if (!CollectionUtils.isEmpty(peopleAlreadyServing) && peopleAlreadyServing.contains(person)) {
				if (this.getPeopleServingDutyById(duty, currentEventRoster).contains(person.getId())) { 
					// if this person is already doing THIS exact duty today, don't let them do it again!
					personPreferenceRanking.put(person, DUTY_CHANCE_NEVER);
				} else {
					// if this person is already doing something today, make it as unlikely as possible to do something else
					personPreferenceRanking.put(person, Math.min(this.getDutyPreference(person, duty), DUTY_CHANCE_LOWEST));
				}
			} else {
				personPreferenceRanking.put(person, this.getDutyPreference(person, duty));
			}
		}
		
		// build list for enumerated distribution
		final List<Pair<Person,Double>> itemWeights = personPreferenceRanking.entrySet().stream()
			.filter(e -> e.getValue() > DUTY_CHANCE_LOWEST)
			.map(e -> new Pair<Person, Double>(e.getKey(), Double.valueOf(e.getValue())))
			.collect(Collectors.toList());
		
		// if there's nobody available w/ a duty chance > 0, add all the 0s to the distribution with a weight of 1 (equal chance)
		if(itemWeights.isEmpty()) {
			itemWeights.addAll(
					personPreferenceRanking.entrySet().stream()
					.filter(e -> e.getValue() == DUTY_CHANCE_LOWEST)
					.map(e -> new Pair<Person, Double>(e.getKey(), 1d))
					.collect(Collectors.toList())
					);
		}

		return CollectionUtils.isEmpty(itemWeights) 
				? Optional.empty() : Optional.of(new EnumeratedDistribution<>(itemWeights).sample());
	}
	
	private Set<Long> getPeopleServingDutyById(@NonNull final Duty duty, @NonNull final EventRoster currentEventRoster){
		return getPeopleServingDuty(duty, currentEventRoster).stream().map(Person::getId).collect(Collectors.toSet());
	}

	private Set<Person> getPeopleServingDuty(@NonNull final Duty duty, @NonNull final EventRoster currentEventRoster) {
		if(currentEventRoster.getDutiesAndPeople() == null) {
			return Collections.emptySet();
		}
		
		return currentEventRoster.getDutiesAndPeople().stream()
			.filter(e -> e.getKey() != null && e.getValue() != null)
			.filter(e -> e.getKey().getId() == duty.getId())
			.map(Entry::getValue)
			.collect(Collectors.toSet());
	}

	private Set<Person> getPeopleWhoServed(final EventRoster er) {
		if(er == null) {
			return Collections.emptySet();
		}
		
		return er.getDutiesAndPeople().stream().map(Entry::getValue).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	private int getDutyPreference(@NonNull final Person p, @NonNull final Duty duty) {
		Optional<PersonDuty> personDuty = p.getDuties().stream().filter(pd -> pd.getDuty().getId() == duty.getId()).findFirst();
		
		if(personDuty.isPresent()) {
			return personDuty.get().getWeightedPreference();
		} else {
			return -1;
		}
	}
	
	public void updateDutiesFromDutyPreference(@NonNull final Person person, @NonNull final List<DutyPreference> dutyPreferences) {
		for(DutyPreference df : dutyPreferences) {
			final Duty duty = this.dutyRepos.findOne(df.getDutyId());
			final int prefRanking = df.getPreference();
			
			person.addOrUpdateDutyAndPreference(duty, prefRanking);
		}
		
		this.save(person);
	}

	@Transactional
	public void updateDutiesFromFormPost(@NonNull final Person person, @NonNull final MultiValueMap<String, String> parameters) {
		logger.debug("updateDutesForPersonFromFormPost({})", person.getId());

		final Map<Long, Integer> dutyPrefs = new HashMap<>();

		for (final Iterator<Entry<String, List<String>>> iter = parameters.entrySet().iterator(); iter.hasNext();) {
			final Entry<String, List<String>> entry = iter.next();
			final String key = entry.getKey();
			final List<String> vals = entry.getValue();

			if (key != null && vals != null && !vals.isEmpty() && key.startsWith("duty_")) {
				final long dutyId = Long.parseLong(key.split("_")[1]);
				final int prefRanking = Integer.parseInt(vals.get(0));
				dutyPrefs.put(dutyId, prefRanking);
			}
		}

		logger.info("Duty Prefs: {}", dutyPrefs);

		boolean personUpdated = false;
		for (final Map.Entry<Long, Integer> entry : dutyPrefs.entrySet()) {
			final Long dutyId = entry.getKey();

			if (dutyId != null && dutyId >= 0) {
				final Duty duty = this.dutyRepos.findOne(dutyId);
				final int prefRanking = entry.getValue();

				person.addOrUpdateDutyAndPreference(duty, prefRanking);
				personUpdated = true;
			}
		}

		if (personUpdated) {
			this.save(person);
		}
	}

}
