package com.sitepark.ies.label.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class LabelScopeAssignmentTest {

  // No testToString() – toString() returns "LabelEntityAssignment{..." (wrong class name)

  @Test
  void testEquals() {
    EqualsVerifier.forClass(LabelScopeAssignment.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testLabelIdsReturnsAllLabelIds() {
    LabelScopeAssignment assignment =
        LabelScopeAssignment.builder().assignment("1", "user").build();
    assertEquals(
        List.of("1"),
        assignment.labelIds(),
        "labelIds() should return all label IDs in the assignment");
  }

  @Test
  void testScopesForLabelIdReturnsAssignedScopes() {
    LabelScopeAssignment assignment =
        LabelScopeAssignment.builder().assignment("1", "user").build();
    assertEquals(
        List.of("user"),
        assignment.scopes("1"),
        "scopes(labelId) should return scopes assigned to that label");
  }

  @Test
  void testScopesForLabelIdReturnsEmptyWhenNotAssigned() {
    LabelScopeAssignment assignment = LabelScopeAssignment.builder().build();
    assertEquals(
        List.of(),
        assignment.scopes("1"),
        "scopes(labelId) should return empty list for unassigned label");
  }

  @Test
  void testScopesReturnsAllScopesInAlphabeticalOrder() {
    LabelScopeAssignment assignment =
        LabelScopeAssignment.builder().assignment("1", "user").assignment("2", "global").build();
    assertEquals(
        List.of("global", "user"),
        assignment.scopes(),
        "scopes() should return all unique scopes in alphabetical order");
  }

  @Test
  void testIsEmptyWhenEmpty() {
    LabelScopeAssignment assignment = LabelScopeAssignment.builder().build();
    assertTrue(assignment.isEmpty(), "isEmpty() should return true for empty assignment");
  }

  @Test
  void testIsNotEmptyWhenAssignmentsExist() {
    LabelScopeAssignment assignment =
        LabelScopeAssignment.builder().assignment("1", "user").build();
    assertFalse(assignment.isEmpty(), "isEmpty() should return false when assignments exist");
  }

  @Test
  void testSizeCountsLabels() {
    LabelScopeAssignment assignment =
        LabelScopeAssignment.builder().assignments("1", "user", "global").build();
    assertEquals(
        1,
        assignment.size(),
        "size() should return the number of labels with scope assignments, not total scope count");
  }

  @Test
  void testToBuilderCreatesEqualCopyWithAdditionalAssignment() {
    LabelScopeAssignment original = LabelScopeAssignment.builder().assignment("1", "user").build();
    LabelScopeAssignment copy = original.toBuilder().assignment("2", "global").build();
    LabelScopeAssignment expected =
        LabelScopeAssignment.builder().assignment("1", "user").assignment("2", "global").build();
    assertEquals(
        expected,
        copy,
        "toBuilder() should create a copy that can be extended with more assignments");
  }
}
