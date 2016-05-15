package com.dak.duty.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dak.duty.RosterApplication;
import com.dak.duty.config.SecurityConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RosterApplication.class, SecurityConfig.class })
@WebAppConfiguration
public abstract class ServiceTest {

	protected static boolean isInitialized = false;

	@Autowired
	InitialisationService initService;

	@Before
	public void runOnce() {
		if (ServiceTest.isInitialized) {
			return;
		}

		this.initService.clearAllData();
		this.initService.populateDefaultData();
		ServiceTest.isInitialized = true;
	}
}
