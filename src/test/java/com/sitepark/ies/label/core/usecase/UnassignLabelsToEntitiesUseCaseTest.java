package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

class UnassignLabelsToEntitiesUseCaseTest {

  private static final EntityRef ENTITY_REF = EntityRef.of("user", "user-1");
  private static final String LABEL_ID = "1";

  private LabelRepository repository;
  private LabelEntityAssigner labelEntityAssigner;
  private AuthorizationService accessControl;
  private UnassignLabelsToEntitiesUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.labelEntityAssigner = mock();
    this.accessControl = mock();
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
    this.useCase =
        new UnassignLabelsToEntitiesUseCase(
            this.repository, this.labelEntityAssigner, this.accessControl, clock);
  }

  @Test
  void testUnassignSkipsWhenRequestHasNoEntities() {
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder().labelIdentifiers(b -> b.id(LABEL_ID)).build();

    UnassignLabelsToEntitiesResult result = this.useCase.unassignEntitiesFromLabels(request);

    assertFalse(result.wasUnassigned(), "Should skip when the entity list is empty");
  }

  @Test
  void testUnassignSkipsWhenRequestHasNoLabels() {
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder().entityRefs(b -> b.add(ENTITY_REF)).build();

    UnassignLabelsToEntitiesResult result = this.useCase.unassignEntitiesFromLabels(request);

    assertFalse(result.wasUnassigned(), "Should skip when the label list is empty");
  }

  @Test
  void testUnassignThrowsWhenNotLabelAssignable() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(false);
    when(this.labelEntityAssigner.getEntitiesAssignByLabels(any()))
        .thenReturn(LabelEntityAssignment.builder().assignment(LABEL_ID, ENTITY_REF).build());
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.unassignEntitiesFromLabels(request),
        "Should throw AccessDeniedException when user is not allowed to unassign labels");
  }

  @Test
  void testUnassignSkipsWhenNoneCurrentlyAssigned() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getEntitiesAssignByLabels(any()))
        .thenReturn(LabelEntityAssignment.builder().build());
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    UnassignLabelsToEntitiesResult result = this.useCase.unassignEntitiesFromLabels(request);

    assertFalse(
        result.wasUnassigned(),
        "Should skip when none of the requested assignments currently exist");
  }

  @Test
  void testUnassignCallsLabelEntityAssigner() {
    LabelEntityAssignment existingAssignment =
        LabelEntityAssignment.builder().assignment(LABEL_ID, ENTITY_REF).build();
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getEntitiesAssignByLabels(any())).thenReturn(existingAssignment);
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    this.useCase.unassignEntitiesFromLabels(request);

    verify(this.labelEntityAssigner)
        .unassignEntitiesFromLabels(List.of(LABEL_ID), List.of(ENTITY_REF));
  }

  @Test
  void testUnassignDoesNotCallAssignerWhenNoneCurrentlyAssigned() {
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getEntitiesAssignByLabels(any()))
        .thenReturn(LabelEntityAssignment.builder().build());
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    this.useCase.unassignEntitiesFromLabels(request);

    verify(this.labelEntityAssigner, never()).unassignEntitiesFromLabels(any(), any());
  }

  @Test
  void testUnassignReturnsUnassignedResult() {
    LabelEntityAssignment existingAssignment =
        LabelEntityAssignment.builder().assignment(LABEL_ID, ENTITY_REF).build();
    when(this.accessControl.isLabelAssignable(any())).thenReturn(true);
    when(this.labelEntityAssigner.getEntitiesAssignByLabels(any())).thenReturn(existingAssignment);
    UnassignLabelsToEntitiesRequest request =
        UnassignLabelsToEntitiesRequest.builder()
            .entityRefs(b -> b.add(ENTITY_REF))
            .labelIdentifiers(b -> b.id(LABEL_ID))
            .build();

    UnassignLabelsToEntitiesResult result = this.useCase.unassignEntitiesFromLabels(request);

    assertTrue(
        result.wasUnassigned(), "Should return an Unassigned result when unassignments were made");
  }
}
