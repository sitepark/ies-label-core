package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelEntityAssigner;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.util.List;

public final class GetLabelsAssignByEntitesUseCase {
  private final LabelEntityAssigner labelEntityAssigner;
  private final AuthorizationService accessControl;

  @Inject
  GetLabelsAssignByEntitesUseCase(
      LabelEntityAssigner labelEntityAssigner, AuthorizationService accessControl) {
    this.labelEntityAssigner = labelEntityAssigner;
    this.accessControl = accessControl;
  }

  public EntityLabelAssignment getLabelsAssignByEntites(List<EntityRef> entityRefs) {
    if (!this.accessControl.isLabelReadable()) {
      throw new AccessDeniedException("Not allowed to read label assignments");
    }
    return this.labelEntityAssigner.getLabelsAssignByEntities(entityRefs);
  }
}
