package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class UpsertLabelRequestTest {

  // No testToString() – toString() returns "CreateUserRequest{..." (wrong class name)

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpsertLabelRequest.class)
        .withPrefabValues(
            Label.class,
            Label.builder().name("label-a").color("ff0000").build(),
            Label.builder().name("label-b").color("00ff00").build())
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }
}
