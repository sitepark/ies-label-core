package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelRepository;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetAllLabelsUseCaseTest {

  private LabelRepository repository;
  private AuthorizationService accessControl;
  private GetAllLabelsUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.accessControl = mock();
    this.useCase = new GetAllLabelsUseCase(this.repository, this.accessControl);
  }

  @Test
  void testGetAllLabelsThrowsWhenNotReadable() {
    when(this.accessControl.isLabelReadable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.getAllLabels(),
        "Should throw AccessDeniedException when user is not allowed to read labels");
  }

  @Test
  void testGetAllLabelsCallsRepository() {
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.repository.getAllLabels()).thenReturn(List.of());

    this.useCase.getAllLabels();

    verify(this.repository).getAllLabels();
  }

  @Test
  void testGetAllLabelsReturnsLabels() {
    Label label = Label.builder().id("1").name("Test").build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.repository.getAllLabels()).thenReturn(List.of(label));

    List<Label> result = this.useCase.getAllLabels();

    assertEquals(List.of(label), result, "Should return the labels from the repository");
  }
}
