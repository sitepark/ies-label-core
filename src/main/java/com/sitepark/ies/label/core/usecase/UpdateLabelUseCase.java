package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.exception.LabelNotFoundException;
import com.sitepark.ies.label.core.domain.value.LabelScopeAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdateLabelUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final LabelRepository repository;
  private final LabelScopeAssigner scopeAssigner;
  private final AuthorizationService accessControl;
  private final PatchService<Label> patchService;
  private final Clock clock;

  @Inject
  UpdateLabelUseCase(
      LabelRepository repository,
      LabelScopeAssigner scopeAssigner,
      AuthorizationService accessControl,
      PatchServiceFactory patchServiceFactory,
      Clock clock) {
    this.repository = repository;
    this.scopeAssigner = scopeAssigner;
    this.accessControl = accessControl;
    this.patchService = patchServiceFactory.createPatchService(Label.class);
    this.clock = clock;
  }

  public UpdateLabelResult updateLabel(UpdateLabelRequest request) {

    if (!this.accessControl.isLabelManagable()) {
      throw new AccessDeniedException("User is not allowed to update labels.");
    }

    Label newLabel;
    if (request.label().id() == null) {
      newLabel = this.toLabelWithId(request.label());
    } else {
      this.validateAnchor(request.label());
      newLabel = request.label();
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update label: {}", newLabel);
    }

    Label oldLabel =
        this.repository
            .get(newLabel.id())
            .orElseThrow(
                () -> new LabelNotFoundException("No label with ID " + newLabel.id() + " found."))
            .toBuilder()
            .build();

    Instant timestamp = Instant.now(this.clock);

    Label joinedLabel = this.joinForUpdate(oldLabel, newLabel);

    PatchDocument patch = this.patchService.createPatch(oldLabel, joinedLabel);

    LabelUpdateResult labelUpdateResult;

    if (patch.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip update, label with ID {} is unchanged.", joinedLabel.id());
      }
      labelUpdateResult = LabelUpdateResult.unchanged();
    } else {
      this.repository.update(joinedLabel);

      PatchDocument revertPatch = this.patchService.createPatch(joinedLabel, oldLabel);
      labelUpdateResult = LabelUpdateResult.updated(joinedLabel.name(), patch, revertPatch);
    }

    ReassignScopesToLabelsResult reassignScopesToLabelsResult;
    if (!request.scopes().isEmpty()) {
      reassignScopesToLabelsResult =
          this.reassignRolesToUsers(List.of(joinedLabel.id()), request.scopes());
    } else {
      reassignScopesToLabelsResult = ReassignScopesToLabelsResult.skipped();
    }

    return new UpdateLabelResult(
        joinedLabel.id(), timestamp, labelUpdateResult, reassignScopesToLabelsResult);
  }

  private Label toLabelWithId(Label label) {
    if (label.id() == null) {
      if (label.anchor() != null) {
        String id =
            this.repository
                .resolveAnchor(label.anchor())
                .orElseThrow(() -> new AnchorNotFoundException(label.anchor()));
        return label.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException("Neither id nor anchor is specified to update the label.");
    }
    return label;
  }

  private void validateAnchor(Label label) {
    if (label.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(label.anchor());
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(label.id())) {
              throw new AnchorAlreadyExistsException(label.anchor(), owner);
            }
          });
    }
  }

  private ReassignScopesToLabelsResult reassignRolesToUsers(
      List<String> labelIds, List<String> scopes) {
    LabelScopeAssignment assignments = this.scopeAssigner.getScopesAssignByLabels(labelIds);
    LabelScopeAssignment effectiveUnassignment =
        effectiveUnassignment(labelIds, scopes, assignments);
    LabelScopeAssignment effectiveAssignments = effectiveAssignments(labelIds, scopes, assignments);

    if (effectiveAssignments.isEmpty() && effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective reassignments found, skipping");
      }
      return ReassignScopesToLabelsResult.skipped();
    }

    for (String labelId : effectiveUnassignment.labelIds()) {
      this.scopeAssigner.unassignScopesFromLabels(
          List.of(labelId), effectiveUnassignment.scopes(labelId));
    }

    for (String labelId : effectiveAssignments.labelIds()) {
      this.scopeAssigner.assignScopesToLabels(
          List.of(labelId), effectiveAssignments.scopes(labelId));
    }

    Instant timestamp = Instant.now(this.clock);

    return ReassignScopesToLabelsResult.reassigned(
        effectiveAssignments, effectiveUnassignment, timestamp);
  }

  private Label joinForUpdate(Label storedLabel, Label updateLabel) {
    Label.Builder builder = updateLabel.toBuilder();
    if (updateLabel.anchor() == null && storedLabel.anchor() != null) {
      builder.anchor(storedLabel.anchor());
    }

    return builder.build();
  }

  private LabelScopeAssignment effectiveAssignments(
      List<String> userIds, List<String> scopes, LabelScopeAssignment assignments) {

    LabelScopeAssignment.Builder builder = LabelScopeAssignment.builder();

    for (String userId : userIds) {
      List<String> assignedScopes = assignments.scopes(userId);
      List<String> effectiveScopes =
          scopes.stream().filter(Predicate.not(assignedScopes::contains)).toList();
      if (!effectiveScopes.isEmpty()) {
        builder.assignments(userId, effectiveScopes);
      }
    }

    return builder.build();
  }

  private LabelScopeAssignment effectiveUnassignment(
      List<String> labelIds, List<String> scopse, LabelScopeAssignment assignments) {

    LabelScopeAssignment.Builder builder = LabelScopeAssignment.builder();

    for (String labelId : labelIds) {

      List<String> assignedScopes = assignments.scopes(labelId);
      List<String> effectiveScopes =
          assignedScopes.stream().filter(Predicate.not(scopse::contains)).toList();
      if (!effectiveScopes.isEmpty()) {
        builder.assignments(labelId, effectiveScopes);
      }
    }

    return builder.build();
  }
}
