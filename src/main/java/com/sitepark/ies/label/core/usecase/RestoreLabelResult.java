package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a label restore operation.
 *
 * <p>This sealed interface represents the outcome of attempting to restore a label. The operation
 * can either successfully restore the label or skip restoration if the label already exists.
 *
 * @see Restored indicates the label was successfully restored
 * @see Skipped indicates restoration was skipped because label already exists
 */
public sealed interface RestoreLabelResult {

  /**
   * Label ID of the restore operation target.
   *
   * @return the label ID
   */
  @NotNull
  String labelId();

  /**
   * Result indicating the label was successfully restored.
   *
   * @param labelId the ID of the restored label
   * @param snapshot snapshot of the restored label data including scope assignments
   * @param timestamp when the restore occurred
   */
  record Restored(
      @NotNull String labelId, @NotNull LabelSnapshot snapshot, @NotNull Instant timestamp)
      implements RestoreLabelResult {}

  /**
   * Result indicating restoration was skipped because the label already exists.
   *
   * @param labelId the ID of the label that already exists
   * @param reason explanation why restoration was skipped
   */
  record Skipped(@NotNull String labelId, @NotNull String reason) implements RestoreLabelResult {}

  /**
   * Factory method for creating a restored result.
   *
   * @param labelId the ID of the restored label
   * @param snapshot snapshot of the restored label data
   * @param timestamp when the restore occurred
   * @return a Restored result
   */
  static RestoreLabelResult restored(String labelId, LabelSnapshot snapshot, Instant timestamp) {
    return new Restored(labelId, snapshot, timestamp);
  }

  /**
   * Factory method for creating a skipped result.
   *
   * @param labelId the ID of the label that already exists
   * @param reason explanation why restoration was skipped
   * @return a Skipped result
   */
  static RestoreLabelResult skipped(String labelId, String reason) {
    return new Skipped(labelId, reason);
  }
}
