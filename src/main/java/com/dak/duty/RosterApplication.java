package com.dak.duty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.dak.duty.repository")
public class RosterApplication {

	public static void main(final String[] args) {
		final SpringApplication springApplication = new SpringApplication(RosterApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter("./roster.pid"));
		springApplication.run(args);
	}
}
