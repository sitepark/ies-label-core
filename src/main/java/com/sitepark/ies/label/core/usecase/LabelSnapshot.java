package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.Collections;
import java.util.List;

public record LabelSnapshot(Label label, List<EntityRef> entityRefs, List<String> scopeIds) {
  public LabelSnapshot {
    entityRefs = entityRefs != null ? List.copyOf(entityRefs) : Collections.emptyList();
    scopeIds = scopeIds != null ? List.copyOf(scopeIds) : Collections.emptyList();
  }

  public List<EntityRef> entityRefs() {
    return List.copyOf(entityRefs);
  }

  public List<String> scopeIds() {
    return List.copyOf(scopeIds);
  }
}
