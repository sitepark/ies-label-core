package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.label.core.domain.service.IdentifierResolver;
import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelEntityAssigner;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Use case for assigning entities to labels.
 *
 * <p>This use case assigns one or more entities to one or more labels. It returns a result
 * indicating whether assignments were made or skipped (if no effective changes).
 *
 * <p><b>Permission Required:</b> Entity Write Access ({@link
 * AuthorizationService#isLabelAssignable(List)})
 */
public final class AssignLabelsToEntitiesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final LabelRepository repository;
  private final LabelEntityAssigner labelEntityAssigner;
  private final AuthorizationService authorizationService;
  private final Clock clock;

  @Inject
  AssignLabelsToEntitiesUseCase(
      LabelRepository repository,
      LabelEntityAssigner labelAssigner,
      AuthorizationService accessControl,
      Clock clock) {

    this.repository = repository;
    this.labelEntityAssigner = labelAssigner;
    this.authorizationService = accessControl;
    this.clock = clock;
  }

  /**
   * Assigns entities to labels.
   *
   * @param request the assignment request containing label identifiers and entity references
   * @return the result containing effective assignments and timestamp, or skipped result
   * @throws AccessDeniedException if the user is not allowed to assign labels to entities
   */
  @NotNull
  public AssignLabelsToEntitiesResult assignEntitiesToLabels(
      @NotNull AssignLabelsToEntitiesRequest request) {

    if (request.isEmpty()) {
      return AssignLabelsToEntitiesResult.skipped();
    }

    List<String> labelIds =
        IdentifierResolver.create(this.repository).resolve(request.labelIdentifiers());

    if (!this.authorizationService.isLabelAssignable(request.entityRefs())) {
      throw new AccessDeniedException("It is not allowed to assign label to entities.");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign entities to labels({}) -> entities({})", labelIds, request.entityRefs());
    }

    EntityLabelAssignment effectiveAssignments =
        effectiveAssignments(request.entityRefs(), labelIds);

    if (effectiveAssignments.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective assignments found, skipping");
      }
      return AssignLabelsToEntitiesResult.skipped();
    }

    this.labelEntityAssigner.assignEntitiesToLabels(labelIds, request.entityRefs());

    Instant timestamp = Instant.now(this.clock);

    return AssignLabelsToEntitiesResult.assigned(effectiveAssignments, timestamp);
  }

  private EntityLabelAssignment effectiveAssignments(
      List<EntityRef> entityRefs, List<String> labelIds) {

    EntityLabelAssignment assignments =
        this.labelEntityAssigner.getLabelsAssignByEntities(entityRefs);

    EntityLabelAssignment.Builder builder = EntityLabelAssignment.builder();

    for (EntityRef entityRef : entityRefs) {
      List<String> effectiveLabelIds =
          labelIds.stream()
              .filter(Predicate.not(assignments.labelIds(entityRef)::contains))
              .toList();
      if (!effectiveLabelIds.isEmpty()) {
        builder.assignments(entityRef, effectiveLabelIds);
      }
    }

    return builder.build();
  }
}
