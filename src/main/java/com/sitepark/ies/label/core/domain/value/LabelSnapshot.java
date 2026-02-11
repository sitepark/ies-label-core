package com.sitepark.ies.label.core.domain.value;

import com.sitepark.ies.label.core.domain.entity.Label;
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
 * @param scopes the list of scope IDs associated with this label
 */
@Immutable
public record LabelSnapshot(@NotNull Label label, @NotNull List<String> scopes) {

  /**
   * Canonical constructor that ensures immutability by creating defensive copies of the scope IDs
   * list.
   *
   * @param label the label entity, must not be null
   * @param scopes the list of scope IDs, must not be null (empty list if no scopes)
   */
  public LabelSnapshot {
    scopes = scopes != null ? List.copyOf(scopes) : Collections.emptyList();
  }

  /**
   * Returns an immutable copy of the scope IDs list.
   *
   * @return an immutable list of scope IDs
   */
  @Override
  public List<String> scopes() {
    return List.copyOf(scopes);
  }
}
