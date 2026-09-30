package com.sitepark.ies.label.core.usecase;

import java.util.Collections;
import java.util.List;

public record SearchLabelsRequest(String term, List<String> scopes) {
  public SearchLabelsRequest {
    scopes = scopes != null ? List.copyOf(scopes) : Collections.emptyList();
  }

  @Override
  public List<String> scopes() {
    return List.copyOf(scopes);
  }
}
