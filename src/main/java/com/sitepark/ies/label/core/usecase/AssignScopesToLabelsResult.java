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
 *   <li>{@link Assigned} - Scopes were successfully assigned to labels
 *   <li>{@link Skipped} - The scope assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Assigned} variant contains assignment information that can be used for audit
 * logging or tracking what scopes were assigned to which labels.
 */
public sealed interface AssignScopesToLabelsResult {

  /**
   * Result when scopes were successfully assigned.
   *
   * @param assignments the effective label-scope assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Assigned(@NotNull LabelScopeAssignment assignments, @NotNull Instant timestamp)
      implements AssignScopesToLabelsResult {}

  /** Result when the scope assignment was skipped. */
  record Skipped() implements AssignScopesToLabelsResult {}

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective label-scope assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static AssignScopesToLabelsResult assigned(
      @NotNull LabelScopeAssignment assignments, @NotNull Instant timestamp) {
    return new Assigned(assignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static AssignScopesToLabelsResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if scopes were assigned, false if skipped
   */
  default boolean wasAssigned() {
    return this instanceof Assigned;
  }
}
