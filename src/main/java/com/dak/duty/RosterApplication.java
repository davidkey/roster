package com.dak.duty;

import java.util.Random;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@Configuration
public class RosterApplication {

	@Value("${security.encoder.strength}")
	private Integer ENCODER_STRENGTH;

	public static void main(final String[] args) {
		final SpringApplication springApplication = new SpringApplication(RosterApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter("./roster.pid"));
		springApplication.run(args);
	}

	/**
	 * For viewing h2 console (in-mem database / schema). Use this url: jdbc:h2:mem:testdb
	 * @return
	 */
	@Bean
	public ServletRegistrationBean h2servletRegistration() {
		final ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
		registration.addUrlMappings("/console/*");
		return registration;
	}

	@Bean
	public Random getRand() {
		return new Random();
	}

	@Bean
	public BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder(this.ENCODER_STRENGTH);
	}
}
