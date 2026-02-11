package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.value.LabelScopeAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.util.List;

public final class GetScopesAssignByLabelsUseCase {
  private final LabelScopeAssigner labelScopeAssigner;
  private final AuthorizationService accessControl;

  @Inject
  GetScopesAssignByLabelsUseCase(
      LabelScopeAssigner labelScopeAssigner, AuthorizationService accessControl) {
    this.labelScopeAssigner = labelScopeAssigner;
    this.accessControl = accessControl;
  }

  public LabelScopeAssignment getPrivilegesAssignByRoles(List<String> labelIds) {
    if (!this.accessControl.isLabelReadable()) {
      throw new AccessDeniedException("Not allowed to read label assignments");
    }
    return this.labelScopeAssigner.getScopesAssignByLabels(labelIds);
  }
}
