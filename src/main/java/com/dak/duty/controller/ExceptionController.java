package com.dak.duty.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {

   @ExceptionHandler(value = {Exception.class, RuntimeException.class})
   public ModelAndView getDefaultErrorView(Exception ex, HttpServletRequest request, HttpServletResponse response){
      
      ModelAndView model = new ModelAndView("error");
      model.addObject("now", new Date());
      model.addObject("requestUrl", request.getRequestURL().toString());
      model.addObject("exception", ex);
      model.addObject("stackTrace", ExceptionUtils.getStackTrace(ex));
      return model;
   }
}
