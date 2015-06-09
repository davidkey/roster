package com.dak.duty.exception;

public class InvalidPasswordException extends RuntimeException {
   private static final long serialVersionUID = -8858709340683421263L;

   public InvalidPasswordException(final String msg){
      super(msg);
   }
}
