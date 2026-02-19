package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.domain.value.LabelSnapshot;
import com.sitepark.ies.label.core.port.LabelRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertLabelUseCaseTest {

  private static final Instant TIMESTAMP = Instant.parse("2024-01-01T00:00:00Z");
  private static final Label LABEL_WITH_ID =
      Label.builder().id("1").name("Test Label").color("ff0000").build();
  private static final Label LABEL_WITHOUT_ID =
      Label.builder().name("New Label").color("00ff00").build();

  private LabelRepository repository;
  private CreateLabelUseCase createLabelUseCase;
  private UpdateLabelUseCase updateLabelUseCase;
  private UpsertLabelUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.createLabelUseCase = mock();
    this.updateLabelUseCase = mock();
    this.useCase =
        new UpsertLabelUseCase(this.repository, this.createLabelUseCase, this.updateLabelUseCase);
  }

  @Test
  void testUpsertCreatesWhenLabelHasNoId() {
    LabelSnapshot snapshot = new LabelSnapshot(LABEL_WITHOUT_ID, List.of());
    AssignScopesToLabelsResult scopeResult = AssignScopesToLabelsResult.skipped();
    CreateLabelResult createResult = new CreateLabelResult("2", snapshot, scopeResult, TIMESTAMP);
    when(this.createLabelUseCase.createLabel(any())).thenReturn(createResult);
    UpsertLabelRequest request = UpsertLabelRequest.builder().label(LABEL_WITHOUT_ID).build();

    UpsertLabelResult result = this.useCase.upsertLabel(request);

    assertTrue(
        result instanceof UpsertLabelResult.Created,
        "Should return a Created result when label has no ID");
  }

  @Test
  void testUpsertCallsCreateWhenLabelHasNoId() {
    LabelSnapshot snapshot = new LabelSnapshot(LABEL_WITHOUT_ID, List.of());
    AssignScopesToLabelsResult scopeResult = AssignScopesToLabelsResult.skipped();
    CreateLabelResult createResult = new CreateLabelResult("2", snapshot, scopeResult, TIMESTAMP);
    when(this.createLabelUseCase.createLabel(any())).thenReturn(createResult);
    UpsertLabelRequest request = UpsertLabelRequest.builder().label(LABEL_WITHOUT_ID).build();

    this.useCase.upsertLabel(request);

    verify(this.createLabelUseCase).createLabel(any());
  }

  @Test
  void testUpsertUpdatesWhenLabelHasId() {
    UpdateLabelResult updateResult =
        new UpdateLabelResult(
            "1", TIMESTAMP, LabelUpdateResult.unchanged(), ReassignScopesToLabelsResult.skipped());
    when(this.updateLabelUseCase.updateLabel(any())).thenReturn(updateResult);
    UpsertLabelRequest request = UpsertLabelRequest.builder().label(LABEL_WITH_ID).build();

    UpsertLabelResult result = this.useCase.upsertLabel(request);

    assertFalse(
        result instanceof UpsertLabelResult.Created,
        "Should return an Updated result when label has an ID");
  }

  @Test
  void testUpsertCallsUpdateWhenLabelHasId() {
    UpdateLabelResult updateResult =
        new UpdateLabelResult(
            "1", TIMESTAMP, LabelUpdateResult.unchanged(), ReassignScopesToLabelsResult.skipped());
    when(this.updateLabelUseCase.updateLabel(any())).thenReturn(updateResult);
    UpsertLabelRequest request = UpsertLabelRequest.builder().label(LABEL_WITH_ID).build();

    this.useCase.upsertLabel(request);

    verify(this.updateLabelUseCase).updateLabel(any());
  }

  @Test
  void testUpsertResolvesAnchorBeforeDeciding() {
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    UpdateLabelResult updateResult =
        new UpdateLabelResult(
            "1", TIMESTAMP, LabelUpdateResult.unchanged(), ReassignScopesToLabelsResult.skipped());
    when(this.updateLabelUseCase.updateLabel(any())).thenReturn(updateResult);
    Label labelWithAnchorOnly =
        Label.builder().anchor("my-label").name("Test").color("ff0000").build();
    UpsertLabelRequest request = UpsertLabelRequest.builder().label(labelWithAnchorOnly).build();

    UpsertLabelResult result = this.useCase.upsertLabel(request);

    assertFalse(
        result instanceof UpsertLabelResult.Created,
        "Should update (not create) when anchor resolves to an existing ID");
  }
}
