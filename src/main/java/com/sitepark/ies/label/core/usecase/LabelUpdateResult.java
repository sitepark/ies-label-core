package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a user data update (without role assignments).
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Unchanged} - The user data was identical to stored data
 *   <li>{@link Updated} - The user data was successfully updated
 * </ul>
 */
public sealed interface LabelUpdateResult {

  /** Result when no changes were detected in user data. */
  record Unchanged() implements LabelUpdateResult {}

  /**
   * Result when the user data was successfully updated.
   *
   * @param labelName the display name of the user
   * @param patch the forward patch (old state to new state)
   * @param revertPatch the revert patch (new state to old state)
   */
  record Updated(
      @NotNull String labelName, @NotNull PatchDocument patch, @NotNull PatchDocument revertPatch)
      implements LabelUpdateResult {}

  /**
   * Factory method for an unchanged result.
   *
   * @return unchanged result
   */
  static LabelUpdateResult unchanged() {
    return new Unchanged();
  }

  /**
   * Factory method for an updated result.
   *
   * @param labelName the display name
   * @param patch the forward patch
   * @param revertPatch the revert patch
   * @return updated result
   */
  static LabelUpdateResult updated(
      @NotNull String labelName, @NotNull PatchDocument patch, @NotNull PatchDocument revertPatch) {
    return new Updated(labelName, patch, revertPatch);
  }
}
