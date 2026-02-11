package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllLabelsUseCase {

  private final LabelRepository repository;

  private final AuthorizationService accessControl;

  @Inject
  GetAllLabelsUseCase(LabelRepository repository, AuthorizationService accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public List<Label> getAllLabels() {

    if (!this.accessControl.isLabelReadable()) {
      throw new AccessDeniedException("Not allowed to read labels");
    }

    return this.repository.getAllLabels();
  }
}
