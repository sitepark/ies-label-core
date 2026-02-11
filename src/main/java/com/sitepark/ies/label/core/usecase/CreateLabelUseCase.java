package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Use case for creating a new label.
 *
 * <p>This use case creates a label and optionally assigns it to specified scopes. It returns a
 * {@link CreateLabelResult} containing the created label's ID, a snapshot for audit logging, and
 * the creation timestamp.
 *
 * <p><b>Permission Required:</b> Label Manager ({@link AuthorizationService#isLabelManagable()})
 */
public final class CreateLabelUseCase {

  private final LabelRepository repository;

  private final LabelScopeAssigner scopeAssigner;

  private final AuthorizationService accessControl;

  private final Clock clock;

  @Inject
  CreateLabelUseCase(
      LabelRepository repository,
      LabelScopeAssigner scopeAssigner,
      AuthorizationService accessControl,
      Clock clock) {
    this.repository = repository;
    this.scopeAssigner = scopeAssigner;
    this.accessControl = accessControl;
    this.clock = clock;
  }

  /**
   * Creates a new label and optionally assigns it to scopes.
   *
   * @param request the label creation request containing the label and scope IDs
   * @return the result containing label ID, snapshot, and timestamp
   * @throws AccessDeniedException if the user is not a label manager
   */
  @NotNull
  public CreateLabelResult createLabel(@NotNull CreateLabelRequest request) {
    if (!this.accessControl.isLabelManagable()) {
      throw new AccessDeniedException("User is not allowed to create labels.");
    }

    Instant timestamp = Instant.now(this.clock);
    String labelId = this.repository.create(request.label());

    List<String> scopeIds = request.scopes();
    if (!scopeIds.isEmpty()) {
      this.scopeAssigner.assignScopesToLabels(List.of(labelId), scopeIds);
    }

    Label createdLabel = this.repository.get(labelId).orElseThrow();
    LabelSnapshot snapshot = new LabelSnapshot(createdLabel, scopeIds);

    return new CreateLabelResult(labelId, snapshot, timestamp);
  }
}
