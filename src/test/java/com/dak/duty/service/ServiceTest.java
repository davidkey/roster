package com.dak.duty.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/servlet-context-test.xml"})
public abstract class ServiceTest {

   protected static boolean isInitialized = false;
   
   @Autowired
   InitialisationService initService;

   
   @Before
   public void runOnce(){
      if(isInitialized){
         return;
      }
      
      initService.clearAllData();
      initService.populateDefaultData();
      isInitialized = true;
   }
}
