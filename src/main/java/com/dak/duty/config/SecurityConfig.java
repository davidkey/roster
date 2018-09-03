package com.dak.duty.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dak.duty.config.jwt.JwtAuthenticationEntryPoint;
import com.dak.duty.config.jwt.JwtAuthorizationTokenFilter;
import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.SecurityEvaluationContextExtension;

import lombok.extern.slf4j.Slf4j;

@Profile("!oldSecurity")
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;
	
	@Value("${jwt.signingKey}")
	private String signingKey;
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private PersonRepository personRepos;


	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		.csrf().disable()
		.cors().configurationSource(corsConfigurationSource()).and() 
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.authorizeRequests()
			.antMatchers("/css/**", "/fonts/**", "/js/**", "/login").permitAll()     
			.antMatchers("/account/**").fullyAuthenticated()
			.anyRequest().permitAll();

		// Custom JWT based security filter
		final JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(signingKey, personRepos);
		
		httpSecurity
			.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

		// disable page caching
		httpSecurity
			.headers()
				.frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
				.cacheControl();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
		log.debug("DaoAuthenticationProvider authProvider()");
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(this.userDetailsService);
		authProvider.setPasswordEncoder(encoder);
		return authProvider;
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public SecurityEvaluationContextExtension getSecurityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}


}