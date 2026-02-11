package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelEntityAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of an entity-to-label assignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Assigned} - Entities were successfully assigned to labels
 *   <li>{@link Skipped} - The entity assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Assigned} variant contains assignment information that can be used for audit
 * logging or tracking which entities were assigned to which labels.
 */
public sealed interface AssignEntitiesToLabelsResult {

  /**
   * Gets the entity-label assignments.
   *
   * @return the entity-label assignments
   */
  @NotNull
  LabelEntityAssignment assignments();

  /**
   * Result when entities were successfully assigned.
   *
   * @param assignments the effective entity-label assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Assigned(@NotNull LabelEntityAssignment assignments, @NotNull Instant timestamp)
      implements AssignEntitiesToLabelsResult {}

  /**
   * Result when the entities assignment was skipped.
   *
   * @param assignments empty assignments (no effective changes)
   */
  record Skipped(@NotNull LabelEntityAssignment assignments)
      implements AssignEntitiesToLabelsResult {
    /** Creates a Skipped result with empty assignments. */
    public Skipped() {
      this(LabelEntityAssignment.builder().build());
    }
  }

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective entity-label assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static AssignEntitiesToLabelsResult assigned(
      @NotNull LabelEntityAssignment assignments, @NotNull Instant timestamp) {
    return new Assigned(assignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static AssignEntitiesToLabelsResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if entities were assigned, false if skipped
   */
  default boolean wasAssigned() {
    return this instanceof Assigned;
  }
}
