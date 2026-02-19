package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchLabelsRequestTest {

  @Test
  void testNullScopesNormalizedToEmptyList() {
    SearchLabelsRequest request = new SearchLabelsRequest("term", null);
    assertEquals(List.of(), request.scopes(), "null scopes should be normalized to an empty list");
  }
}
