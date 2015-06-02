package com.dak.duty.api.util;

import lombok.Getter;

@Getter
public class JsonResponse {
   private final ResponseStatus response;
   private final String detail;
   
   public JsonResponse(final ResponseStatus r, final String d){
      this.response = r;
      this.detail = d;
   }
   
   public static enum ResponseStatus {
      OK, ERROR;
   }
}
