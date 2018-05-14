package com.dak.duty.config;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VelocityConfig {

	public VelocityConfig() {
		
	}

	@Bean
	VelocityEngine velocityEngine() {
		Properties properties = new Properties();
		properties.setProperty("input.encoding", "UTF-8");
		properties.setProperty("output.encoding", "UTF-8");
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return new VelocityEngine(properties);
	}

}
