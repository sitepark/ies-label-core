package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelEntityAssigner;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetLabelsAssignByEntitesUseCaseTest {

  private static final EntityRef ENTITY_REF = EntityRef.of("user", "user-1");

  private LabelEntityAssigner labelEntityAssigner;
  private AuthorizationService accessControl;
  private GetLabelsAssignByEntitesUseCase useCase;

  @BeforeEach
  void setUp() {
    this.labelEntityAssigner = mock();
    this.accessControl = mock();
    this.useCase =
        new GetLabelsAssignByEntitesUseCase(this.labelEntityAssigner, this.accessControl);
  }

  @Test
  void testGetLabelsAssignByEntitiesThrowsWhenNotReadable() {
    when(this.accessControl.isLabelReadable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.getLabelsAssignByEntites(List.of(ENTITY_REF)),
        "Should throw AccessDeniedException when user is not allowed to read label assignments");
  }

  @Test
  void testGetLabelsAssignByEntitiesCallsAssigner() {
    EntityLabelAssignment expected = EntityLabelAssignment.builder().build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(List.of(ENTITY_REF)))
        .thenReturn(expected);

    this.useCase.getLabelsAssignByEntites(List.of(ENTITY_REF));

    verify(this.labelEntityAssigner).getLabelsAssignByEntities(List.of(ENTITY_REF));
  }

  @Test
  void testGetLabelsAssignByEntitiesReturnsAssignment() {
    EntityLabelAssignment expected =
        EntityLabelAssignment.builder().assignment(ENTITY_REF, "1").build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.labelEntityAssigner.getLabelsAssignByEntities(List.of(ENTITY_REF)))
        .thenReturn(expected);

    EntityLabelAssignment result = this.useCase.getLabelsAssignByEntites(List.of(ENTITY_REF));

    assertEquals(expected, result, "Should return the entity-label assignments from the assigner");
  }
}
