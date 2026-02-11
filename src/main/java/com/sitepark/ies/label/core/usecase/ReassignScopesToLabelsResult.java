package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelScopeAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a scope assignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Reassigned} - Scopes were successfully assigned to labels
 *   <li>{@link Skipped} - The scope assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Reassigned} variant contains assignment information that can be used for audit
 * logging or tracking what scopes were assigned to which labels.
 */
public sealed interface ReassignScopesToLabelsResult {

  /**
   * Result when scopes were successfully assigned.
   *
   * @param assignments the effective label-scope assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Reassigned(
      @NotNull LabelScopeAssignment assignments,
      @NotNull LabelScopeAssignment unassignments,
      @NotNull Instant timestamp)
      implements ReassignScopesToLabelsResult {}

  /** Result when the scope assignment was skipped. */
  record Skipped() implements ReassignScopesToLabelsResult {}

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective label-scope assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static ReassignScopesToLabelsResult reassigned(
      @NotNull LabelScopeAssignment assignments,
      @NotNull LabelScopeAssignment unassignments,
      @NotNull Instant timestamp) {
    return new Reassigned(assignments, unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static ReassignScopesToLabelsResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if scopes were assigned, false if skipped
   */
  default boolean wasReassigned() {
    return this instanceof Reassigned;
  }
}
