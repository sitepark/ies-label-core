package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.sharedkernel.base.Identifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemoveLabelRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RemoveLabelRequest.class)
        .withPrefabValues(Identifier.class, Identifier.ofId("1"), Identifier.ofId("2"))
        .verify();
  }
}
