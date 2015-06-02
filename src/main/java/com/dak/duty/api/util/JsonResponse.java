package com.dak.duty.api.util;

public class JsonResponse {
   public ResponseStatus response;
   public String detail;
   
   public JsonResponse(final ResponseStatus r, final String d){
      this.response = r;
      this.detail = d;
   }
   
   public static enum ResponseStatus {
      OK, ERROR;
   }
}
