package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Request for restoring a previously removed label.
 *
 * @param snapshot the snapshot containing the label and scope assignments to restore
 * @param auditParentId optional audit parent ID for linking related audit entries
 */
public record RestoreLabelRequest(
    @NotNull LabelSnapshot snapshot, @Nullable String auditParentId) {}
