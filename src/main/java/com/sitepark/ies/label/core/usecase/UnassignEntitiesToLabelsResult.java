package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelEntityAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of an entity-from-label unassignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Unassigned} - Entities were successfully unassigned from labels
 *   <li>{@link Skipped} - The entity unassignment was skipped (no effective unassignments)
 * </ul>
 *
 * <p>The {@link Unassigned} variant contains unassignment information that can be used for audit
 * logging or tracking which entities were unassigned from which labels.
 */
public sealed interface UnassignEntitiesToLabelsResult {

  /**
   * Gets the entity-label unassignments.
   *
   * @return the entity-label unassignments
   */
  @NotNull
  LabelEntityAssignment unassignments();

  /**
   * Result when entities were successfully unassigned.
   *
   * @param unassignments the effective entity-label unassignments that were made
   * @param timestamp the timestamp when the unassignment occurred
   */
  record Unassigned(@NotNull LabelEntityAssignment unassignments, @NotNull Instant timestamp)
      implements UnassignEntitiesToLabelsResult {}

  /**
   * Result when the entities unassignment was skipped.
   *
   * @param unassignments empty unassignments (no effective changes)
   */
  record Skipped(@NotNull LabelEntityAssignment unassignments)
      implements UnassignEntitiesToLabelsResult {
    /** Creates a Skipped result with empty assignments. */
    public Skipped() {
      this(LabelEntityAssignment.builder().build());
    }
  }

  /**
   * Factory method for unassigned result.
   *
   * @param assignments the effective entity-label unassignments
   * @param timestamp the unassignment timestamp
   * @return unassigned result
   */
  static UnassignEntitiesToLabelsResult unassigned(
      @NotNull LabelEntityAssignment assignments, @NotNull Instant timestamp) {
    return new Unassigned(assignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty unassignments
   */
  static UnassignEntitiesToLabelsResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful unassignment (not skipped).
   *
   * @return true if entities were unassigned, false if skipped
   */
  default boolean wasUnassigned() {
    return this instanceof Unassigned;
  }
}
