package com.sitepark.ies.label.core.port;

import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.List;

public interface AuthorizationService {
  boolean isLabelManagable();

  boolean isLabelAssignable(List<EntityRef> entityRefs);

  boolean isLabelReadable();
}
