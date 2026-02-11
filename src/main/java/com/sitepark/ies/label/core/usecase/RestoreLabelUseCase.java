package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Use case for restoring a previously removed label.
 *
 * <p>This use case restores a label from a snapshot, including its scope assignments. It checks if
 * the label already exists and skips restoration if so.
 *
 * <p><b>Permission Required:</b> Label Manager ({@link AuthorizationService#isLabelManagable()})
 */
public final class RestoreLabelUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final LabelRepository repository;
  private final LabelScopeAssigner scopeAssigner;
  private final AuthorizationService accessControl;
  private final Clock clock;

  @Inject
  RestoreLabelUseCase(
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
   * Restores a label from a snapshot.
   *
   * @param request the restore request containing the label snapshot
   * @return the result indicating whether the label was restored or skipped
   * @throws AccessDeniedException if the user is not a label manager
   * @throws IllegalArgumentException if the label ID is null or empty
   * @throws AnchorAlreadyExistsException if the anchor is already assigned to another label
   */
  public RestoreLabelResult restoreLabel(RestoreLabelRequest request) {

    Label label = request.snapshot().label();
    List<String> scopeIds = request.snapshot().scopes();

    this.validateLabel(label);

    this.checkAccessControl(label);

    if (this.repository.get(label.id()).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, label with ID {} already exists.", label.id());
      }
      return RestoreLabelResult.skipped(
          label.id(), "Label with ID " + label.id() + " already exists");
    }

    this.validateAnchor(label);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("restore label: {}", label);
    }

    Instant timestamp = Instant.now(this.clock);

    LabelSnapshot snapshot = new LabelSnapshot(label, scopeIds);

    this.repository.restore(label);
    if (!scopeIds.isEmpty()) {
      String labelId = label.id();
      assert labelId != null : "label.id() was validated in validateLabel()";
      this.scopeAssigner.assignScopesToLabels(List.of(labelId), scopeIds);
    }

    return RestoreLabelResult.restored(label.id(), snapshot, timestamp);
  }

  private void validateLabel(Label label) {
    if (label.id() == null || label.id().isBlank()) {
      throw new IllegalArgumentException("The id of the label must not be null or empty.");
    }
    if (label.name() == null || label.name().isBlank()) {
      throw new IllegalArgumentException("The name of the label must not be null or empty.");
    }
  }

  private void checkAccessControl(Label label) {
    if (!this.accessControl.isLabelManagable()) {
      throw new AccessDeniedException("Not allowed to restore label " + label);
    }
  }

  private void validateAnchor(Label label) {
    if (label.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(label.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(label.anchor(), owner);
          });
    }
  }
}
