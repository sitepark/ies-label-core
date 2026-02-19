package com.sitepark.ies.label.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class EntityLabelAssignmentTest {

  private static final EntityRef ENTITY_REF = EntityRef.of("user", "user-1");

  @Test
  void testEquals() {
    EqualsVerifier.forClass(EntityLabelAssignment.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testEntityRefsReturnsAllEntityRefs() {
    EntityLabelAssignment assignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "1").build();
    assertEquals(
        List.of(ENTITY_REF),
        assignment.entityRefs(),
        "entityRefs() should return all entity refs in the assignment");
  }

  @Test
  void testLabelIdsForEntityRefReturnsAssignedLabels() {
    EntityLabelAssignment assignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "label-1").build();
    assertEquals(
        List.of("label-1"),
        assignment.labelIds(ENTITY_REF),
        "labelIds(entityRef) should return label IDs assigned to that entity ref");
  }

  @Test
  void testLabelIdsForEntityRefReturnsEmptyWhenNotAssigned() {
    EntityLabelAssignment assignment = EntityLabelAssignment.builder().build();
    assertEquals(
        List.of(),
        assignment.labelIds(ENTITY_REF),
        "labelIds(entityRef) should return empty list for unassigned entity ref");
  }

  @Test
  void testLabelIdsReturnsAllLabelIds() {
    EntityLabelAssignment assignment =
        EntityLabelAssignment.builder()
            .assignment(ENTITY_REF, "1")
            .assignment(EntityRef.of("role", "role-1"), "2")
            .build();
    assertEquals(
        List.of("1", "2"),
        assignment.labelIds(),
        "labelIds() should return all unique label IDs across all entity refs");
  }

  @Test
  void testIsEmptyWhenEmpty() {
    EntityLabelAssignment assignment = EntityLabelAssignment.builder().build();
    assertTrue(assignment.isEmpty(), "isEmpty() should return true for empty assignment");
  }

  @Test
  void testIsNotEmptyWhenAssignmentsExist() {
    EntityLabelAssignment assignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "1").build();
    assertFalse(assignment.isEmpty(), "isEmpty() should return false when assignments exist");
  }

  @Test
  void testSizeCountsAllAssignments() {
    EntityLabelAssignment assignment =
        EntityLabelAssignment.builder().assignments(ENTITY_REF, "1", "2").build();
    assertEquals(
        2, assignment.size(), "size() should return the total number of entity-label pairs");
  }

  @Test
  void testToBuilderCreatesEqualCopyWithAdditionalAssignment() {
    EntityLabelAssignment original =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "1").build();
    EntityRef otherRef = EntityRef.of("role", "role-1");
    EntityLabelAssignment copy = original.toBuilder().assignment(otherRef, "2").build();
    EntityLabelAssignment expected =
        EntityLabelAssignment.builder()
            .assignment(ENTITY_REF, "1")
            .assignment(otherRef, "2")
            .build();
    assertEquals(
        expected,
        copy,
        "toBuilder() should create a copy that can be extended with more assignments");
  }
}
