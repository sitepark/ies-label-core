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

class GetLabelsByIdsUseCaseTest {

  private LabelRepository repository;
  private AuthorizationService accessControl;
  private GetLabelsByIdsUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.accessControl = mock();
    this.useCase = new GetLabelsByIdsUseCase(this.repository, this.accessControl);
  }

  @Test
  void testGetLabelsByIdsThrowsWhenNotReadable() {
    when(this.accessControl.isLabelReadable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.getLabelsByIds(List.of("1")),
        "Should throw AccessDeniedException when user is not allowed to read labels");
  }

  @Test
  void testGetLabelsByIdsCallsRepository() {
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.repository.getByIds(List.of("1"))).thenReturn(List.of());

    this.useCase.getLabelsByIds(List.of("1"));

    verify(this.repository).getByIds(List.of("1"));
  }

  @Test
  void testGetLabelsByIdsReturnsLabels() {
    Label label = Label.builder().id("1").name("Test").build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.repository.getByIds(List.of("1"))).thenReturn(List.of(label));

    List<Label> result = this.useCase.getLabelsByIds(List.of("1"));

    assertEquals(List.of(label), result, "Should return the labels from the repository");
  }
}
