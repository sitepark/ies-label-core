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

public final class ReassignLabelsToEntitiesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final LabelRepository repository;
  private final LabelEntityAssigner labelEntityAssigner;
  private final AuthorizationService authorizationService;
  private final Clock clock;

  @Inject
  ReassignLabelsToEntitiesUseCase(
      LabelRepository repository,
      LabelEntityAssigner labelEntityAssigner,
      AuthorizationService authorizationService,
      Clock clock) {

    this.repository = repository;
    this.labelEntityAssigner = labelEntityAssigner;
    this.authorizationService = authorizationService;
    this.clock = clock;
  }

  public ReassignLabelsToEntitiesResult reassignLabelsToEntities(
      ReassignLabelsToEntitiesRequest request) {

    if (request.isEmpty()) {
      return ReassignLabelsToEntitiesResult.skipped();
    }

    List<String> labelIds =
        IdentifierResolver.create(this.repository).resolve(request.labelIdentifiers());

    if (!this.authorizationService.isLabelAssignable(request.entityRefs())) {
      throw new AccessDeniedException("It is not allowed to reassign label to entities.");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "reassign entities to labels({}) -> entities({})", labelIds, request.entityRefs());
    }

    return this.reassignLabelToEntities(request.entityRefs(), labelIds);
  }

  private ReassignLabelsToEntitiesResult reassignLabelToEntities(
      List<EntityRef> entityRefs, List<String> labelIds) {

    EntityLabelAssignment assignments =
        this.labelEntityAssigner.getLabelsAssignByEntities(entityRefs);
    EntityLabelAssignment effectiveUnassignment =
        effectiveUnassignment(entityRefs, labelIds, assignments);
    EntityLabelAssignment effectiveAssignments =
        effectiveAssignments(entityRefs, labelIds, assignments);

    if (effectiveAssignments.isEmpty() && effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective reassignments found, skipping");
      }
      return ReassignLabelsToEntitiesResult.skipped();
    }

    for (EntityRef entityRef : effectiveUnassignment.entityRefs()) {
      this.labelEntityAssigner.unassignEntitiesFromLabels(
          effectiveUnassignment.labelIds(entityRef), List.of(entityRef));
    }

    for (EntityRef entityRef : effectiveAssignments.entityRefs()) {
      this.labelEntityAssigner.assignEntitiesToLabels(
          effectiveAssignments.labelIds(entityRef), List.of(entityRef));
    }

    Instant timestamp = Instant.now(this.clock);

    return ReassignLabelsToEntitiesResult.reassigned(
        effectiveAssignments, effectiveUnassignment, timestamp);
  }

  private EntityLabelAssignment effectiveAssignments(
      List<EntityRef> entityRefs, List<String> labelIds, EntityLabelAssignment assignments) {

    EntityLabelAssignment.Builder builder = EntityLabelAssignment.builder();

    for (EntityRef entityRef : entityRefs) {
      List<String> assignedLabels = assignments.labelIds(entityRef);
      List<String> effectiveLabelIds =
          labelIds.stream().filter(Predicate.not(assignedLabels::contains)).toList();
      if (!effectiveLabelIds.isEmpty()) {
        builder.assignments(entityRef, effectiveLabelIds);
      }
    }

    return builder.build();
  }

  private EntityLabelAssignment effectiveUnassignment(
      List<EntityRef> entityRefs, List<String> labelIds, EntityLabelAssignment assignments) {

    EntityLabelAssignment.Builder builder = EntityLabelAssignment.builder();

    for (EntityRef entityRef : entityRefs) {

      List<String> assignedLabels = assignments.labelIds(entityRef);
      List<String> effectiveLabelIds =
          assignedLabels.stream().filter(Predicate.not(labelIds::contains)).toList();
      if (!effectiveLabelIds.isEmpty()) {
        builder.assignments(entityRef, effectiveLabelIds);
      }
    }

    return builder.build();
  }
}
