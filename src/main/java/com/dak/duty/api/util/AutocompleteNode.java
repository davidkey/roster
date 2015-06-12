package com.dak.duty.api.util;

import lombok.Getter;

@Getter
public class AutocompleteNode {
   private final String value;
   private final String data;
   
   public AutocompleteNode(final String value, final String data){
      this.value = value;
      this.data = data;
   }
}
