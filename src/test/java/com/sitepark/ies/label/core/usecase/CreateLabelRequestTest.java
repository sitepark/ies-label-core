package com.sitepark.ies.label.core.usecase;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreateLabelRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreateLabelRequest.class).verify();
  }
}
