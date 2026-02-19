package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class UnassignLabelsToEntitiesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UnassignLabelsToEntitiesRequest.class)
        .withPrefabValues(EntityRef.class, EntityRef.of("user", "1"), EntityRef.of("role", "2"))
        .withPrefabValues(Identifier.class, Identifier.ofId("1"), Identifier.ofId("2"))
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }
}
