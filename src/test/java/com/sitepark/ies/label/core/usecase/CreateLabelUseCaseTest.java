package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.entity.Label;
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

class CreateLabelUseCaseTest {

  private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");
  private static final Label TEST_LABEL =
      Label.builder().name("Test Label").color("ff0000").build();
  private static final String LABEL_ID = "1";

  private LabelRepository repository;
  private LabelScopeAssigner scopeAssigner;
  private AuthorizationService accessControl;
  private CreateLabelUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.scopeAssigner = mock();
    this.accessControl = mock();
    Clock clock = Clock.fixed(FIXED_INSTANT, ZoneId.of("UTC"));
    this.useCase =
        new CreateLabelUseCase(this.repository, this.scopeAssigner, this.accessControl, clock);
  }

  @Test
  void testCreateLabelThrowsWhenNotLabelManager() {
    when(this.accessControl.isLabelManagable()).thenReturn(false);
    CreateLabelRequest request = CreateLabelRequest.builder().label(TEST_LABEL).build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.createLabel(request),
        "Should throw AccessDeniedException when user is not a label manager");
  }

  @Test
  void testCreateLabelCallsRepositoryCreate() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn(LABEL_ID);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    CreateLabelRequest request = CreateLabelRequest.builder().label(TEST_LABEL).build();

    this.useCase.createLabel(request);

    verify(this.repository).create(TEST_LABEL);
  }

  @Test
  void testCreateLabelFetchesCreatedLabelFromRepository() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn(LABEL_ID);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    CreateLabelRequest request = CreateLabelRequest.builder().label(TEST_LABEL).build();

    this.useCase.createLabel(request);

    verify(this.repository).get(LABEL_ID);
  }

  @Test
  void testCreateLabelReturnsCreatedLabelId() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn(LABEL_ID);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    CreateLabelRequest request = CreateLabelRequest.builder().label(TEST_LABEL).build();

    CreateLabelResult result = this.useCase.createLabel(request);

    assertEquals(LABEL_ID, result.labelId(), "Result should contain the created label ID");
  }

  @Test
  void testCreateLabelReturnsTimestamp() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn(LABEL_ID);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    CreateLabelRequest request = CreateLabelRequest.builder().label(TEST_LABEL).build();

    CreateLabelResult result = this.useCase.createLabel(request);

    assertEquals(FIXED_INSTANT, result.timestamp(), "Result should contain the creation timestamp");
  }

  @Test
  void testCreateLabelAssignsScopesWhenProvided() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn(LABEL_ID);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    CreateLabelRequest request =
        CreateLabelRequest.builder().label(TEST_LABEL).scopes(b -> b.add("user")).build();

    this.useCase.createLabel(request);

    verify(this.scopeAssigner).assignScopesToLabels(List.of(LABEL_ID), List.of("user"));
  }

  @Test
  void testCreateLabelDoesNotAssignScopesWhenNoneProvided() {
    when(this.accessControl.isLabelManagable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn(LABEL_ID);
    when(this.repository.get(LABEL_ID)).thenReturn(Optional.of(TEST_LABEL));
    CreateLabelRequest request = CreateLabelRequest.builder().label(TEST_LABEL).build();

    this.useCase.createLabel(request);

    verify(this.scopeAssigner, never()).assignScopesToLabels(any(), any());
  }
}
