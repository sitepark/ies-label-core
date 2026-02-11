package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.service.IdentifierResolver;
import com.sitepark.ies.label.core.domain.value.LabelEntityAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelEntityAssigner;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Use case for unassigning entities from labels.
 *
 * <p>This use case removes entity-to-label assignments. It returns a result indicating whether
 * unassignments were made or skipped (if no effective changes).
 *
 * <p><b>Permission Required:</b> Entity Write Access ({@link
 * AuthorizationService#isLabelAssignable(List)})
 */
public final class UnassignEntitiesFromLabelsUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final LabelRepository repository;
  private final LabelEntityAssigner labelEntityAssigner;
  private final AuthorizationService accessControl;
  private final Clock clock;

  @Inject
  UnassignEntitiesFromLabelsUseCase(
      LabelRepository repository,
      LabelEntityAssigner labelAssigner,
      AuthorizationService accessControl,
      Clock clock) {

    this.repository = repository;
    this.labelEntityAssigner = labelAssigner;
    this.accessControl = accessControl;
    this.clock = clock;
  }

  /**
   * Unassigns entities from labels.
   *
   * @param request the unassignment request containing label identifiers and entity references
   * @return the result containing effective unassignments and timestamp, or skipped result
   * @throws AccessDeniedException if the user is not allowed to unassign labels from entities
   */
  @NotNull
  public UnassignEntitiesToLabelsResult unassignEntitiesFromLabels(
      @NotNull UnassignEntitiesFromLabelsRequest request) {

    if (request.isEmpty()) {
      return UnassignEntitiesToLabelsResult.skipped();
    }

    List<String> labelIds =
        IdentifierResolver.create(this.repository).resolve(request.labelIdentifiers());

    if (!this.accessControl.isLabelAssignable(request.entityRefs())) {
      throw new AccessDeniedException("It is not allowed to unassign labels from entities.");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "unassign entities from labels({}) -> entities({})", labelIds, request.entityRefs());
    }

    LabelEntityAssignment effectiveUnassignment =
        effectiveUnassignments(labelIds, request.entityRefs());

    if (effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective unassignments found, skipping");
      }
      return UnassignEntitiesToLabelsResult.skipped();
    }

    this.labelEntityAssigner.unassignEntitiesFromLabels(labelIds, request.entityRefs());

    Instant timestamp = Instant.now(this.clock);

    return UnassignEntitiesToLabelsResult.unassigned(effectiveUnassignment, timestamp);
  }

  private LabelEntityAssignment effectiveUnassignments(
      List<String> labelIds, List<EntityRef> entityRefs) {

    LabelEntityAssignment assignments =
        this.labelEntityAssigner.getEntitiesAssignByLabels(labelIds);

    LabelEntityAssignment.Builder builder = LabelEntityAssignment.builder();

    for (String labelId : labelIds) {
      List<EntityRef> effectiveEntityRefs =
          entityRefs.stream().filter(assignments.entityRefs(labelId)::contains).toList();
      if (!effectiveEntityRefs.isEmpty()) {
        builder.assignments(labelId, effectiveEntityRefs);
      }
    }

    return builder.build();
  }
}
