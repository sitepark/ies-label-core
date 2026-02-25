package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import com.sitepark.ies.label.core.domain.value.LabelEntityAssignment;
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

class AssignLabelsToEntitiesUseCaseTest {

  private static final EntityRef ENTITY_REF = EntityRef.of("user", "user-1");
  private static final String LABEL_ID = "1";

  private LabelRepository repository;
  private LabelEntityAssigner labelEntityAssigner;
  private AuthorizationService accessControl;
  private AssignLabelsToEntitiesUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.labelEntityAssigner = mock();
    this.accessControl = mock();
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
    this.useCase =
        new AssignLabelsToEntitiesUseCase(
            this.repository, this.labelEntityAssigner, this.accessControl, clock);
  }

  @Test
  void testAssignSkipsWhenRequestHasNoEntities() {
    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder().labelIdentifiers(b -> b.id(LABEL_ID)).build();

    AssignLabelsToEntitiesResult result = this.useCase.assignEntitiesToLabels(request);

    assertFalse(result.wasAssigned(), "Should skip when the entity list is empty");
  }

  @Test
  void testAssignSkipsWhenRequestHasNoLabels() {
    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder().entityRefs(b -> b.add(ENTITY_REF)).build();

    AssignLabelsToEntitiesResult result = this.useCase.assignEntitiesToLabels(request);

    assertFalse(result.wasAssigned(), "Should skip when the label list is empty");
  }

  @Test
  void testAssignThrowsWhenNotLabelAssignable() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(false);
    when(this.labelEntityAssigner.getEntitiesAssignByLabels(any()))
        .thenReturn(LabelEntityAssignment.builder().build());
    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.assignEntitiesToLabels(request),
        "Should throw AccessDeniedException when user is not allowed to assign labels");
  }

  @Test
  void testAssignCallsLabelEntityAssigner() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any()))
        .thenReturn(EntityLabelAssignment.builder().build());
    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    this.useCase.assignEntitiesToLabels(request);

    verify(this.labelEntityAssigner).assignEntitiesToLabels(List.of(LABEL_ID), List.of(ENTITY_REF));
  }

  @Test
  void testAssignSkipsWhenAllAlreadyAssigned() {
    EntityLabelAssignment existingAssignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, LABEL_ID).build();
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any())).thenReturn(existingAssignment);

    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    AssignLabelsToEntitiesResult result = this.useCase.assignEntitiesToLabels(request);

    assertFalse(result.wasAssigned(), "Should skip when all requested assignments already exist");
  }

  @Test
  void testAssignDoesNotCallAssignerWhenAllAlreadyAssigned() {
    EntityLabelAssignment existingAssignment =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, LABEL_ID).build();
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any())).thenReturn(existingAssignment);
    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    this.useCase.assignEntitiesToLabels(request);

    verify(this.labelEntityAssigner, never()).assignEntitiesToLabels(any(), any());
  }

  @Test
  void testAssignReturnsAssignedResult() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(any()))
        .thenReturn(EntityLabelAssignment.builder().build());
    AssignLabelsToEntitiesRequest request =
        AssignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    AssignLabelsToEntitiesResult result = this.useCase.assignEntitiesToLabels(request);

    assertTrue(result.wasAssigned(), "Should return an Assigned result when assignments were made");
  }
}
