package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import jakarta.inject.Inject;

public class UpsertLabelUseCase {

  private final LabelRepository repository;
  private final CreateLabelUseCase createLabelUseCase;
  private final UpdateLabelUseCase updateLabelUseCase;

  @Inject
  UpsertLabelUseCase(
      LabelRepository repository,
      CreateLabelUseCase createLabelUseCase,
      UpdateLabelUseCase updateLabelUseCase) {
    this.repository = repository;
    this.createLabelUseCase = createLabelUseCase;
    this.updateLabelUseCase = updateLabelUseCase;
  }

  public UpsertLabelResult upsertLabel(UpsertLabelRequest request) {

    Label labelResolved = this.toLabelWithId(request.label());

    if (labelResolved.id() == null) {
      CreateLabelResult result =
          this.createLabelUseCase.createLabel(
              CreateLabelRequest.builder()
                  .label(labelResolved)
                  .scopes(configure -> configure.addAll(request.scopes()))
                  .build());
      return UpsertLabelResult.created(result.labelId(), result);
    } else {
      UpdateLabelResult result =
          this.updateLabelUseCase.updateLabel(
              UpdateLabelRequest.builder()
                  .label(labelResolved)
                  .scopes(configure -> configure.addAll(request.scopes()))
                  .build());
      return UpsertLabelResult.updated(labelResolved.id(), result);
    }
  }

  private Label toLabelWithId(Label label) {
    if (label.id() == null && label.anchor() != null) {
      return this.repository
          .resolveAnchor(label.anchor())
          .map(s -> label.toBuilder().id(s).build())
          .orElse(label);
    } else if (label.id() != null && label.anchor() != null) {
      this.repository
          .resolveAnchor(label.anchor())
          .ifPresent(
              owner -> {
                if (!owner.equals(label.id())) {
                  throw new AnchorAlreadyExistsException(label.anchor(), owner);
                }
              });
    }
    return label;
  }
}
