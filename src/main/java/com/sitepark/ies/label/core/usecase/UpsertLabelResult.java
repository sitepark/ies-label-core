package com.sitepark.ies.label.core.usecase;

import org.jetbrains.annotations.NotNull;

public sealed interface UpsertLabelResult {

  record Created(@NotNull String labelId, @NotNull CreateLabelResult updateLabelResult)
      implements UpsertLabelResult {}

  record Updated(@NotNull String labelId, @NotNull UpdateLabelResult updateLabelResult)
      implements UpsertLabelResult {}

  static Created created(@NotNull String labelId, @NotNull CreateLabelResult updateLabelResult) {
    return new Created(labelId, updateLabelResult);
  }

  static Updated updated(@NotNull String labelId, @NotNull UpdateLabelResult updateLabelResult) {
    return new Updated(labelId, updateLabelResult);
  }
}
