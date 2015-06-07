package com.dak.duty.api.util;

import java.util.List;

import lombok.Getter;

@Getter
public class AutocompleteResponse {
   private final String query;
   private final List<AutocompleteNode> suggestions;
   
   public AutocompleteResponse(final String query, final List<AutocompleteNode> suggestions){
      this.query = query;
      this.suggestions = suggestions;
   }
}
