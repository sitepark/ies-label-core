package com.sitepark.ies.label.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.List;
import org.junit.jupiter.api.Test;

class LabelSnapshotTest {

  private static final Label LABEL =
      Label.builder().id("1").name("Test Label").color("ff0000").build();

  @Test
  void testNullScopesNormalizedToEmptyList() {
    LabelSnapshot snapshot = new LabelSnapshot(LABEL, null, null);
    assertEquals(List.of(), snapshot.scopes(), "null scopes should be normalized to an empty list");
  }

  @Test
  void testScopesReturnsImmutableList() {
    LabelSnapshot snapshot = new LabelSnapshot(LABEL, List.of("user"), null);
    List<String> scopes = snapshot.scopes();
    assertThrows(
        UnsupportedOperationException.class,
        () -> scopes.add("other"),
        "scopes() should return an unmodifiable list");
  }

  @Test
  void testEntityRefsReturnsImmutableList() {
    LabelSnapshot snapshot = new LabelSnapshot(LABEL, null, List.of(EntityRef.of("user", "2")));
    List<EntityRef> entityRefs = snapshot.entityRefs();
    assertThrows(
        UnsupportedOperationException.class,
        () -> entityRefs.add(EntityRef.of("other", "2")),
        "scopes() should return an unmodifiable list");
  }
}
