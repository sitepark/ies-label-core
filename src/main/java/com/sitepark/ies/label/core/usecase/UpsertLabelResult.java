package com.sitepark.ies.label.core.usecase;

public sealed interface UpsertLabelResult {

  record Created(String labelId, CreateLabelResult createLabelResult)
      implements UpsertLabelResult {}

  record Updated(String labelId, UpdateLabelResult updateLabelResult)
      implements UpsertLabelResult {}

  static Created created(String labelId, CreateLabelResult result) {
    return new Created(labelId, result);
  }

  static Updated updated(String labelId, UpdateLabelResult result) {
    return new Updated(labelId, result);
  }
}
