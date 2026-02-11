package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a label removal operation.
 *
 * <p>A label removal can result in two outcomes:
 *
 * <ul>
 *   <li>{@link Removed} - Label was successfully deleted
 *   <li>{@link Skipped} - Label was not removed (e.g., label not found, validation failed)
 * </ul>
 *
 * <p>Use {@link #wasRemoved()} to check if the label was actually deleted, and {@link #asRemoved()}
 * to access removal details including the snapshot for audit logging.
 */
public sealed interface RemoveLabelResult {

  /**
   * Returns the ID of the label that was processed.
   *
   * @return the label ID
   */
  @NotNull
  String labelId();

  /**
   * Result indicating the label was successfully removed.
   *
   * @param labelId the ID of the removed label
   * @param labelName the display name of the label (for audit logging)
   * @param snapshot the complete snapshot of the label before removal (for audit logging)
   * @param timestamp the exact moment when the removal occurred
   */
  record Removed(
      @NotNull String labelId,
      @NotNull String labelName,
      @NotNull LabelSnapshot snapshot,
      @NotNull Instant timestamp)
      implements RemoveLabelResult {}

  /**
   * Result indicating the label was not removed.
   *
   * @param labelId the ID of the label that was not removed
   * @param reason the reason why the removal was skipped
   */
  record Skipped(@NotNull String labelId, @NotNull String reason) implements RemoveLabelResult {}

  /**
   * Factory method to create a Removed result.
   *
   * @param labelId the ID of the removed label
   * @param labelName the display name of the label
   * @param snapshot the snapshot before removal
   * @param timestamp the timestamp of removal
   * @return a Removed result
   */
  static RemoveLabelResult removed(
      @NotNull String labelId,
      @NotNull String labelName,
      @NotNull LabelSnapshot snapshot,
      @NotNull Instant timestamp) {
    return new Removed(labelId, labelName, snapshot, timestamp);
  }

  /**
   * Factory method to create a Skipped result.
   *
   * @param labelId the ID of the label that was not removed
   * @param reason the reason for skipping
   * @return a Skipped result
   */
  static RemoveLabelResult skipped(@NotNull String labelId, @NotNull String reason) {
    return new Skipped(labelId, reason);
  }

  /**
   * Checks if the label was actually removed.
   *
   * @return true if this is a Removed result, false if Skipped
   */
  default boolean wasRemoved() {
    return this instanceof Removed;
  }

  /**
   * Casts this result to Removed if it represents an actual removal.
   *
   * @return the Removed result, or null if this is Skipped
   */
  @Nullable
  default Removed asRemoved() {
    return this instanceof Removed removed ? removed : null;
  }
}
