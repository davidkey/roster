package com.dak.duty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncoderConfig {
	

	public EncoderConfig() {
		//
	}


	@Bean
	public PasswordEncoder getEncoder(@Value("${security.encoder.strength}") final Integer encoderStrength) {
		return new BCryptPasswordEncoder(encoderStrength);
	}
	
}
