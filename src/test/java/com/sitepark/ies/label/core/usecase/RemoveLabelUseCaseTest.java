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
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveLabelUseCaseTest {

  private static final String LABEL_ID = "1";
  private static final Label TEST_LABEL =
      Label.builder().id(LABEL_ID).name("Test Label").color("ff0000").build();

  private LabelRepository repository;
  private LabelScopeAssigner scopeAssigner;
  private AuthorizationService accessControl;
  private RemoveLabelUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.scopeAssigner = mock();
    this.accessControl = mock();
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
    this.useCase =
        new RemoveLabelUseCase(this.repository, this.scopeAssigner, this.accessControl, clock);
  }

  @Test
  void testRemoveLabelThrowsWhenNotLabelManager() {
    when(this.accessControl.isLabelManagable()).thenReturn(false);
    RemoveLabelRequest request = RemoveLabelRequest.builder().id(LABEL_ID).build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.removeLabel(request),
        "Should throw AccessDeniedException when user is not a label manager");
  }

  @Test
  void testRemoveLabelThrowsWhenLabelNotFound() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.empty());
    when(this.scopeAssigner.getScopesAssignByLabel(any())).thenReturn(List.of());
    RemoveLabelRequest request = RemoveLabelRequest.builder().id(LABEL_ID).build();
    assertThrows(
        LabelNotFoundException.class,
        () -> this.useCase.removeLabel(request),
        "Should throw LabelNotFoundException when label does not exist");
  }

  @Test
  void testRemoveLabelCallsRepositoryRemove() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    when(this.scopeAssigner.getScopesAssignByLabel(LABEL_ID)).thenReturn(List.of());
    RemoveLabelRequest request = RemoveLabelRequest.builder().id(LABEL_ID).build();

    this.useCase.removeLabel(request);

    verify(this.repository).remove(LABEL_ID);
  }

  @Test
  void testRemoveLabelFetchesScopesForSnapshot() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    when(this.scopeAssigner.getScopesAssignByLabel(LABEL_ID)).thenReturn(List.of("user"));
    RemoveLabelRequest request = RemoveLabelRequest.builder().id(LABEL_ID).build();

    this.useCase.removeLabel(request);

    verify(this.scopeAssigner).getScopesAssignByLabel(LABEL_ID);
  }

  @Test
  void testRemoveLabelReturnsRemovedResult() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    when(this.scopeAssigner.getScopesAssignByLabel(LABEL_ID)).thenReturn(List.of());
    RemoveLabelRequest request = RemoveLabelRequest.builder().id(LABEL_ID).build();

    RemoveLabelResult result = this.useCase.removeLabel(request);

    assertTrue(result.wasRemoved(), "Result should indicate that the label was removed");
  }

  @Test
  void testRemoveLabelWithAnchorIdentifier() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of(LABEL_ID));
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    when(this.scopeAssigner.getScopesAssignByLabel(LABEL_ID)).thenReturn(List.of());
    RemoveLabelRequest request =
        RemoveLabelRequest.builder().identifier(Identifier.ofAnchor("test-label")).build();

    this.useCase.removeLabel(request);

    verify(this.repository).remove(LABEL_ID);
  }
}
