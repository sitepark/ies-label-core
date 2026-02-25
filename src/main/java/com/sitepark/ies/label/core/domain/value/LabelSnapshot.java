package com.sitepark.ies.label.core.domain.value;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable snapshot of a label's state including its scope assignments.
 *
 * <p>This snapshot is used for audit logging to capture the complete state of a label at a specific
 * point in time, including all associated scope IDs.
 *
 * @param label the label entity
 * @param scopes the list of scopes associated with this label
 * @param entityRefs the list of entity references associated with this label
 */
@Immutable
public record LabelSnapshot(
    @NotNull Label label, @NotNull List<String> scopes, @NotNull List<EntityRef> entityRefs) {

  public LabelSnapshot {
    scopes = scopes != null ? List.copyOf(scopes) : Collections.emptyList();
    entityRefs = entityRefs != null ? List.copyOf(entityRefs) : Collections.emptyList();
  }

  @Override
  public List<String> scopes() {
    return List.copyOf(scopes);
  }

  @Override
  public List<EntityRef> entityRefs() {
    return List.copyOf(entityRefs);
  }
}
