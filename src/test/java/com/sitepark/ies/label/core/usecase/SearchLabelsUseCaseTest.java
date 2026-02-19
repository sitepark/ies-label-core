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

class SearchLabelsUseCaseTest {

  private LabelRepository repository;
  private AuthorizationService accessControl;
  private SearchLabelsUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.accessControl = mock();
    this.useCase = new SearchLabelsUseCase(this.repository, this.accessControl);
  }

  @Test
  void testSearchLabelsThrowsWhenNotReadable() {
    when(this.accessControl.isLabelReadable()).thenReturn(false);
    SearchLabelsRequest request = new SearchLabelsRequest("test", List.of());
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.searchLabels(request),
        "Should throw AccessDeniedException when user is not allowed to read labels");
  }

  @Test
  void testSearchLabelsCallsRepositoryWithTermAndScopes() {
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.repository.search("test", List.of("user"))).thenReturn(List.of());
    SearchLabelsRequest request = new SearchLabelsRequest("test", List.of("user"));

    this.useCase.searchLabels(request);

    verify(this.repository).search("test", List.of("user"));
  }

  @Test
  void testSearchLabelsReturnsLabels() {
    Label label = Label.builder().id("1").name("Test").build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.repository.search("test", List.of())).thenReturn(List.of(label));
    SearchLabelsRequest request = new SearchLabelsRequest("test", List.of());

    List<Label> result = this.useCase.searchLabels(request);

    assertEquals(List.of(label), result, "Should return the labels found by the repository");
  }
}
