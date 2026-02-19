package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.exception.LabelNotFoundException;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateLabelUseCaseTest {

  private static final String LABEL_ID = "1";
  private static final Label EXISTING_LABEL =
      Label.builder().id(LABEL_ID).name("Old Name").color("ff0000").build();
  private static final Label UPDATED_LABEL =
      Label.builder().id(LABEL_ID).name("New Name").color("ff0000").build();

  private LabelRepository repository;
  private LabelScopeAssigner scopeAssigner;
  private AuthorizationService accessControl;
  private PatchServiceFactory patchServiceFactory;

  @SuppressWarnings("unchecked")
  private PatchService<Label> patchService = mock();

  private UpdateLabelUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.scopeAssigner = mock();
    this.accessControl = mock();
    this.patchServiceFactory = mock();
    this.patchService = mock();
    when(this.patchServiceFactory.createPatchService(Label.class)).thenReturn(this.patchService);
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
    this.useCase =
        new UpdateLabelUseCase(
            this.repository,
            this.scopeAssigner,
            this.accessControl,
            this.patchServiceFactory,
            clock);
  }

  @Test
  void testUpdateLabelThrowsWhenNotLabelManager() {
    when(this.accessControl.isLabelManagable()).thenReturn(false);
    UpdateLabelRequest request = UpdateLabelRequest.builder().label(UPDATED_LABEL).build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.updateLabel(request),
        "Should throw AccessDeniedException when user is not a label manager");
  }

  @Test
  void testUpdateLabelThrowsWhenLabelNotFound() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.empty());
    UpdateLabelRequest request = UpdateLabelRequest.builder().label(UPDATED_LABEL).build();
    assertThrows(
        LabelNotFoundException.class,
        () -> this.useCase.updateLabel(request),
        "Should throw LabelNotFoundException when label does not exist");
  }

  @Test
  void testUpdateLabelCallsRepositoryUpdateWhenChanged() {
    PatchDocument nonEmptyPatch = mock();
    when(nonEmptyPatch.isEmpty()).thenReturn(false);
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(EXISTING_LABEL));
    when(this.patchService.createPatch(any(), any())).thenReturn(nonEmptyPatch);
    when(this.scopeAssigner.getScopesAssignByLabels(any()))
        .thenReturn(
            com.sitepark.ies.label.core.domain.value.LabelScopeAssignment.builder().build());
    UpdateLabelRequest request = UpdateLabelRequest.builder().label(UPDATED_LABEL).build();

    this.useCase.updateLabel(request);

    verify(this.repository).update(UPDATED_LABEL);
  }

  @Test
  void testUpdateLabelSkipsRepositoryUpdateWhenUnchanged() {
    PatchDocument emptyPatch = mock();
    when(emptyPatch.isEmpty()).thenReturn(true);
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(EXISTING_LABEL));
    when(this.patchService.createPatch(any(), any())).thenReturn(emptyPatch);
    UpdateLabelRequest request = UpdateLabelRequest.builder().label(UPDATED_LABEL).build();

    UpdateLabelResult result = this.useCase.updateLabel(request);

    assertTrue(
        result.labelResult() instanceof LabelUpdateResult.Unchanged,
        "Should return an Unchanged result when no fields changed");
  }

  @Test
  void testUpdateLabelResolvesAnchorWhenNoIdProvided() {
    Label labelWithAnchorOnly =
        Label.builder().anchor("my-label").name("New Name").color("ff0000").build();
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of(LABEL_ID));
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(EXISTING_LABEL));
    PatchDocument nonEmptyPatch = mock();
    when(nonEmptyPatch.isEmpty()).thenReturn(false);
    when(this.patchService.createPatch(any(), any())).thenReturn(nonEmptyPatch);
    when(this.scopeAssigner.getScopesAssignByLabels(any()))
        .thenReturn(
            com.sitepark.ies.label.core.domain.value.LabelScopeAssignment.builder().build());
    UpdateLabelRequest request = UpdateLabelRequest.builder().label(labelWithAnchorOnly).build();

    this.useCase.updateLabel(request);

    verify(this.repository).resolveAnchor(any());
  }

  @Test
  void testUpdateLabelReassignsScopesWhenProvided() {
    PatchDocument nonEmptyPatch = mock();
    when(nonEmptyPatch.isEmpty()).thenReturn(false);
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(EXISTING_LABEL));
    when(this.patchService.createPatch(any(), any())).thenReturn(nonEmptyPatch);
    when(this.scopeAssigner.getScopesAssignByLabels(List.of(LABEL_ID)))
        .thenReturn(
            com.sitepark.ies.label.core.domain.value.LabelScopeAssignment.builder().build());
    UpdateLabelRequest request =
        UpdateLabelRequest.builder().label(UPDATED_LABEL).scopes(b -> b.add("user")).build();

    this.useCase.updateLabel(request);

    verify(this.scopeAssigner).getScopesAssignByLabels(List.of(LABEL_ID));
  }
}
