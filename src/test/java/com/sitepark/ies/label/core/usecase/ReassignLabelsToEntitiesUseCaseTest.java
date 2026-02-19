package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelEntityAssigner;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReassignLabelsToEntitiesUseCaseTest {

  private static final EntityRef ENTITY_REF = EntityRef.of("user", "user-1");

  private LabelRepository repository;
  private LabelEntityAssigner labelEntityAssigner;
  private AuthorizationService accessControl;
  private ReassignLabelsToEntitiesUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.labelEntityAssigner = mock();
    this.accessControl = mock();
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
    this.useCase =
        new ReassignLabelsToEntitiesUseCase(
            this.repository, this.labelEntityAssigner, this.accessControl, clock);
  }

  @Test
  void testThrowsWhenNotAssignable() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(false);
    ReassignLabelsToEntitiesRequest request =
        ReassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id("1"))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.reassignLabelsToEntities(request),
        "Should throw AccessDeniedException when user is not allowed to reassign labels");
  }

  @Test
  void testReturnsSkippedWhenNoDifference() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    EntityLabelAssignment currentAssignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "1").build();
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any())).thenReturn(currentAssignment);
    ReassignLabelsToEntitiesRequest request =
        ReassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id("1"))
            .build();

    ReassignLabelsToEntitiesResult result = this.useCase.reassignLabelsToEntities(request);

    assertTrue(
        result instanceof ReassignLabelsToEntitiesResult.Skipped,
        "Should return Skipped when current assignments already match the requested assignments");
  }

  @Test
  void testCallsAssignForNewLabel() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any()))
        .thenReturn(EntityLabelAssignment.builder().build());
    ReassignLabelsToEntitiesRequest request =
        ReassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id("1"))
            .build();

    this.useCase.reassignLabelsToEntities(request);

    verify(this.labelEntityAssigner).assignEntitiesToLabels(List.of("1"), List.of(ENTITY_REF));
  }

  @Test
  void testCallsUnassignForRemovedLabel() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    EntityLabelAssignment currentAssignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "1").build();
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any())).thenReturn(currentAssignment);
    ReassignLabelsToEntitiesRequest request =
        ReassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id("2"))
            .build();

    this.useCase.reassignLabelsToEntities(request);

    verify(this.labelEntityAssigner).unassignEntitiesFromLabels(List.of("1"), List.of(ENTITY_REF));
  }

  @Test
  void testReturnsReassignedResult() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any()))
        .thenReturn(EntityLabelAssignment.builder().build());
    ReassignLabelsToEntitiesRequest request =
        ReassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id("1"))
            .build();

    ReassignLabelsToEntitiesResult result = this.useCase.reassignLabelsToEntities(request);

    assertTrue(
        result.wasReassigned(), "Should return a Reassigned result when assignments were changed");
  }
}
