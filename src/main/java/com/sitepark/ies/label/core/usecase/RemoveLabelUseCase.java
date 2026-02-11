package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.exception.LabelNotFoundException;
import com.sitepark.ies.label.core.domain.service.IdentifierResolver;
import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Use case for removing a label.
 *
 * <p>This use case removes a label from the system. It captures a snapshot of the label (including
 * scope assignments) before removal for audit logging purposes.
 *
 * <p><b>Permission Required:</b> Label Manager ({@link AuthorizationService#isLabelManagable()})
 */
public final class RemoveLabelUseCase {

  private final LabelRepository repository;

  private final LabelScopeAssigner scopeAssigner;

  private final AuthorizationService accessControl;

  private final Clock clock;

  @Inject
  RemoveLabelUseCase(
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
   * Removes a label from the system.
   *
   * @param request the label removal request containing the label identifier
   * @return the result containing label ID, display name, snapshot, and timestamp
   * @throws AccessDeniedException if the user is not a label manager
   * @throws LabelNotFoundException if the label does not exist
   */
  @NotNull
  public RemoveLabelResult removeLabel(@NotNull RemoveLabelRequest request) {

    if (!this.accessControl.isLabelManagable()) {
      throw new AccessDeniedException("User is not allowed to remove labels.");
    }

    String id = this.resolveIdentifier(request.identifier());

    // Create snapshot BEFORE removal (for audit)
    Label label = this.loadLabel(id);
    List<String> scopeIds = this.scopeAssigner.getScopesAssignByLabel(id);

    LabelSnapshot snapshot = new LabelSnapshot(label, scopeIds);
    Instant timestamp = Instant.now(this.clock);

    // Perform removal
    this.repository.remove(id);

    return RemoveLabelResult.removed(id, label.name(), snapshot, timestamp);
  }

  private String resolveIdentifier(Identifier identifier) {
    IdentifierResolver resolver = IdentifierResolver.create(this.repository);
    return resolver.resolve(identifier);
  }

  private Label loadLabel(String id) {
    return this.repository.get(id).orElseThrow(() -> new LabelNotFoundException(id));
  }
}
