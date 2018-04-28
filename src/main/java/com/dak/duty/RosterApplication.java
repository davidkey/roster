package com.dak.duty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class RosterApplication {

	public static void main(final String[] args) {
		final SpringApplication springApplication = new SpringApplication(RosterApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter("./roster.pid"));
		springApplication.run(args);
	}
}
