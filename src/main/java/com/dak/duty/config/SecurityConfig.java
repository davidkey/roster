package com.dak.duty.config;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.dak.duty.security.SecurityEvaluationContextExtension;

// @Configuration
// @EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Order(SecurityProperties.BASIC_AUTH_ORDER - 1) // max order
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	PasswordEncoder encoder;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		LOGGER.debug("configure(http)");

		http.authorizeRequests()
			.antMatchers("/login", "/logout", "/console/**", "/info").permitAll()
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/user/**").hasRole("USER")
			.antMatchers("/h2-console/**").permitAll()
				// .anyRequest().fullyAuthenticated()
		.and()
			.formLogin().loginPage("/login").defaultSuccessUrl("/user").failureUrl("/login?error=true")
		.and()
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).deleteCookies("JSESSIONID").invalidateHttpSession(true)
		.and()
			.exceptionHandling().accessDeniedPage("/error?error=accessdenied")
		.and()
			.csrf().ignoringAntMatchers("/h2-console/**")
		.and()
			.headers().frameOptions().sameOrigin()
			;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		LOGGER.debug("configure(auth)");
		auth.authenticationProvider(this.authProvider());
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		LOGGER.debug("DaoAuthenticationProvider authProvider()");
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
	
	@Bean
	public Random getRand() {
		return new Random();
	}



}
