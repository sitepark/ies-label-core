package com.sitepark.ies.label.core.usecase;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a label update operation.
 *
 * <p>Contains results for both label data changes and role assignment changes. Each aspect is
 * represented independently, allowing for:
 *
 * <ul>
 *   <li>Label unchanged, scopes unchanged
 *   <li>Label unchanged, scopes changed
 *   <li>Label changed, scopes unchanged
 *   <li>Label changed, scopes changed
 * </ul>
 */
public record UpdateLabelResult(
    @NotNull String labelId,
    @NotNull Instant timestamp,
    @NotNull LabelUpdateResult labelResult,
    @NotNull ReassignScopesToLabelsResult scopeReassignmentResult) {

  /**
   * Checks if the label data was changed.
   *
   * @return true if label was updated, false if unchanged
   */
  public boolean hasLabelChanges() {
    return labelResult instanceof LabelUpdateResult.Updated;
  }

  /**
   * Checks if scopes were assigned.
   *
   * @return true if scopes were assigned, false if skipped
   */
  public boolean hasScopeChanges() {
    return scopeReassignmentResult.wasReassigned();
  }

  /**
   * Checks if any changes were made (label or scopes).
   *
   * @return true if label or scopes changed
   */
  public boolean hasAnyChanges() {
    return hasLabelChanges() || hasScopeChanges();
  }

  /**
   * Gets the label update details if the label was changed.
   *
   * @return the Updated result or null if unchanged
   */
  public LabelUpdateResult.Updated getLabelUpdate() {
    return labelResult instanceof LabelUpdateResult.Updated updated ? updated : null;
  }

  /**
   * Gets the role assignment details if scopes were assigned.
   *
   * @return the Assigned result or null if skipped
   */
  public ReassignScopesToLabelsResult.Reassigned getScopeReassignment() {
    return scopeReassignmentResult instanceof ReassignScopesToLabelsResult.Reassigned reassigned
        ? reassigned
        : null;
  }
}
