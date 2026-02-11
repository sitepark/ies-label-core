package com.sitepark.ies.label.core.usecase;

import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public record SearchLabelsRequest(String term, List<String> scopes) {
  public SearchLabelsRequest {
    scopes = scopes != null ? List.copyOf(scopes) : Collections.emptyList();
  }

  public List<String> scopes() {
    return List.copyOf(scopes);
  }
}
