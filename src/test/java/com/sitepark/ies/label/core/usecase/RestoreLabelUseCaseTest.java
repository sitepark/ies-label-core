package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestoreLabelUseCaseTest {

  private static final Label VALID_LABEL =
      Label.builder().id("1").name("Test Label").color("ff0000").build();

  private LabelRepository repository;
  private LabelScopeAssigner scopeAssigner;
  private AuthorizationService accessControl;
  private RestoreLabelUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.scopeAssigner = mock();
    this.accessControl = mock();
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
    this.useCase =
        new RestoreLabelUseCase(this.repository, this.scopeAssigner, this.accessControl, clock);
  }

  @Test
  void testRestoreLabelThrowsWhenLabelIdIsNull() {
    Label labelWithoutId = Label.builder().name("Test").build();
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(labelWithoutId, List.of()), null);
    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restoreLabel(request),
        "Should throw IllegalArgumentException when label has no ID");
  }

  @Test
  void testRestoreLabelThrowsWhenLabelNameIsBlank() {
    Label labelWithoutName = Label.builder().id("1").build();
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(labelWithoutName, List.of()), null);
    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restoreLabel(request),
        "Should throw IllegalArgumentException when label has no name");
  }

  @Test
  void testRestoreLabelThrowsWhenNotLabelManager() {
    when(this.accessControl.isLabelManagable()).thenReturn(false);
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(VALID_LABEL, List.of()), null);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.restoreLabel(request),
        "Should throw AccessDeniedException when user is not a label manager");
  }

  @Test
  void testRestoreLabelReturnsSkippedWhenAlreadyExists() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get("1")).thenReturn(Optional.of(VALID_LABEL));
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(VALID_LABEL, List.of()), null);

    RestoreLabelResult result = this.useCase.restoreLabel(request);

    assertTrue(
        result instanceof RestoreLabelResult.Skipped,
        "Should return a Skipped result when label already exists");
  }

  @Test
  void testRestoreLabelCallsRepositoryRestore() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get("1")).thenReturn(Optional.empty());
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(VALID_LABEL, List.of()), null);

    this.useCase.restoreLabel(request);

    verify(this.repository).restore(VALID_LABEL);
  }

  @Test
  void testRestoreLabelAssignsScopesWhenProvided() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get("1")).thenReturn(Optional.empty());
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(VALID_LABEL, List.of("user")), null);

    this.useCase.restoreLabel(request);

    verify(this.scopeAssigner).assignScopesToLabels(List.of("1"), List.of("user"));
  }

  @Test
  void testRestoreLabelDoesNotAssignScopesWhenNoneProvided() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get("1")).thenReturn(Optional.empty());
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(VALID_LABEL, List.of()), null);

    this.useCase.restoreLabel(request);

    verify(this.scopeAssigner, never()).assignScopesToLabels(any(), any());
  }

  @Test
  void testRestoreLabelReturnsRestoredResult() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.get("1")).thenReturn(Optional.empty());
    RestoreLabelRequest request =
        new RestoreLabelRequest(new LabelSnapshot(VALID_LABEL, List.of()), null);

    RestoreLabelResult result = this.useCase.restoreLabel(request);

    assertTrue(
        result instanceof RestoreLabelResult.Restored,
        "Should return a Restored result when label was restored");
  }
}
