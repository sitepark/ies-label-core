package com.sitepark.ies.label.core.usecase;

import org.jetbrains.annotations.NotNull;

public sealed interface UpsertLabelResult {

  record Created(@NotNull String labelId, @NotNull CreateLabelResult createLabelResult)
      implements UpsertLabelResult {}

  record Updated(@NotNull String labelId, @NotNull UpdateLabelResult updateLabelResult)
      implements UpsertLabelResult {}

  static Created created(@NotNull String labelId, @NotNull CreateLabelResult result) {
    return new Created(labelId, result);
  }

  static Updated updated(@NotNull String labelId, @NotNull UpdateLabelResult result) {
    return new Updated(labelId, result);
  }
}
