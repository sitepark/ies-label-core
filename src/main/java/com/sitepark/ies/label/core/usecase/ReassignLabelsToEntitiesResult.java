package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import java.time.Instant;

/**
 * Result of a label assignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Reassigned} - Labels were successfully assigned to entities
 *   <li>{@link Skipped} - The label assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Reassigned} variant contains assignment information that can be used for audit
 * logging or tracking what labels were assigned to which entities.
 */
public sealed interface ReassignLabelsToEntitiesResult {

  /**
   * Result when labels were successfully assigned.
   *
   * @param assignments the effective entity-label assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Reassigned(
      EntityLabelAssignment assignments, EntityLabelAssignment unassignments, Instant timestamp)
      implements ReassignLabelsToEntitiesResult {}

  /** Result when the label assignment was skipped. */
  record Skipped() implements ReassignLabelsToEntitiesResult {}

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective entity-label assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static ReassignLabelsToEntitiesResult reassigned(
      EntityLabelAssignment assignments, EntityLabelAssignment unassignments, Instant timestamp) {
    return new Reassigned(assignments, unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static ReassignLabelsToEntitiesResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if labels were assigned, false if skipped
   */
  default boolean wasReassigned() {
    return this instanceof Reassigned;
  }
}
