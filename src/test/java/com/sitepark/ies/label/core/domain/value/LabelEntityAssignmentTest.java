package com.sitepark.ies.label.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class LabelEntityAssignmentTest {

  private static final EntityRef ENTITY_REF = EntityRef.of("user", "user-1");

  @Test
  void testEquals() {
    EqualsVerifier.forClass(LabelEntityAssignment.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testLabelIdsReturnsAllLabelIds() {
    LabelEntityAssignment assignment =
        LabelEntityAssignment.builder().assignment("1", ENTITY_REF).build();
    assertEquals(
        List.of("1"),
        assignment.labelIds(),
        "labelIds() should return all label IDs in the assignment");
  }

  @Test
  void testEntityRefsForLabelIdReturnsAssignedEntityRefs() {
    LabelEntityAssignment assignment =
        LabelEntityAssignment.builder().assignment("1", ENTITY_REF).build();
    assertEquals(
        List.of(ENTITY_REF),
        assignment.entityRefs("1"),
        "entityRefs(labelId) should return entity refs assigned to that label");
  }

  @Test
  void testEntityRefsForLabelIdReturnsEmptyWhenNotAssigned() {
    LabelEntityAssignment assignment = LabelEntityAssignment.builder().build();
    assertEquals(
        List.of(),
        assignment.entityRefs("1"),
        "entityRefs(labelId) should return empty list for unassigned label");
  }

  @Test
  void testEntityRefsReturnsCountOfAllEntityRefs() {
    LabelEntityAssignment assignment =
        LabelEntityAssignment.builder()
            .assignment("1", ENTITY_REF)
            .assignment("2", EntityRef.of("role", "role-1"))
            .build();
    assertEquals(
        2,
        assignment.entityRefs().size(),
        "entityRefs() should return all unique entity refs across all labels");
  }

  @Test
  void testIsEmptyWhenEmpty() {
    LabelEntityAssignment assignment = LabelEntityAssignment.builder().build();
    assertTrue(assignment.isEmpty(), "isEmpty() should return true for empty assignment");
  }

  @Test
  void testIsNotEmptyWhenAssignmentsExist() {
    LabelEntityAssignment assignment =
        LabelEntityAssignment.builder().assignment("1", ENTITY_REF).build();
    assertFalse(assignment.isEmpty(), "isEmpty() should return false when assignments exist");
  }

  @Test
  void testSizeCountsAllLabelEntityPairs() {
    LabelEntityAssignment assignment =
        LabelEntityAssignment.builder()
            .assignments("1", ENTITY_REF, EntityRef.of("role", "role-1"))
            .build();
    assertEquals(
        2, assignment.size(), "size() should return the total number of label-entity pairs");
  }

  @Test
  void testToBuilderCreatesEqualCopyWithAdditionalAssignment() {
    LabelEntityAssignment original =
        LabelEntityAssignment.builder().assignment("1", ENTITY_REF).build();
    LabelEntityAssignment copy =
        original.toBuilder().assignment("2", EntityRef.of("role", "role-1")).build();
    LabelEntityAssignment expected =
        LabelEntityAssignment.builder()
            .assignment("1", ENTITY_REF)
            .assignment("2", EntityRef.of("role", "role-1"))
            .build();
    assertEquals(
        expected,
        copy,
        "toBuilder() should create a copy that can be extended with more assignments");
  }
}
