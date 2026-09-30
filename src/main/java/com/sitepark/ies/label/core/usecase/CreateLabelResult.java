package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import java.time.Instant;

/**
 * Result of a label creation operation.
 *
 * <p>This result always indicates successful creation, as label creation either succeeds or throws
 * an exception. It contains the created label's ID, a snapshot of the label state (including scope
 * assignments), and the timestamp of creation for audit logging.
 *
 * @param labelId the unique identifier of the created label
 * @param snapshot the complete snapshot of the created label including scope assignments
 * @param timestamp the exact moment when the label was created
 */
public record CreateLabelResult(
    String labelId,
    LabelSnapshot snapshot,
    AssignScopesToLabelsResult scopeAssignmentResult,
    Instant timestamp) {}
