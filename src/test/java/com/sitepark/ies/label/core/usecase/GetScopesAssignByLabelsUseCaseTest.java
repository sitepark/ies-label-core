package com.sitepark.ies.label.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.domain.value.LabelScopeAssignment;
import com.sitepark.ies.label.core.port.AuthorizationService;
import com.sitepark.ies.label.core.port.LabelScopeAssigner;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetScopesAssignByLabelsUseCaseTest {

  private LabelScopeAssigner labelScopeAssigner;
  private AuthorizationService accessControl;
  private GetScopesAssignByLabelsUseCase useCase;

  @BeforeEach
  void setUp() {
    this.labelScopeAssigner = mock();
    this.accessControl = mock();
    this.useCase = new GetScopesAssignByLabelsUseCase(this.labelScopeAssigner, this.accessControl);
  }

  @Test
  void testGetScopesAssignByLabelsThrowsWhenNotReadable() {
    when(this.accessControl.isLabelReadable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.getPrivilegesAssignByRoles(List.of("1")),
        "Should throw AccessDeniedException when user is not allowed to read label assignments");
  }

  @Test
  void testGetScopesAssignByLabelsCallsAssigner() {
    LabelScopeAssignment expected = LabelScopeAssignment.builder().build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.labelScopeAssigner.getScopesAssignByLabels(List.of("1"))).thenReturn(expected);

    this.useCase.getPrivilegesAssignByRoles(List.of("1"));

    verify(this.labelScopeAssigner).getScopesAssignByLabels(List.of("1"));
  }

  @Test
  void testGetScopesAssignByLabelsReturnsAssignment() {
    LabelScopeAssignment expected = LabelScopeAssignment.builder().assignments("1", "user").build();
    when(this.accessControl.isLabelReadable()).thenReturn(true);
    when(this.labelScopeAssigner.getScopesAssignByLabels(List.of("1"))).thenReturn(expected);

    LabelScopeAssignment result = this.useCase.getPrivilegesAssignByRoles(List.of("1"));

    assertEquals(expected, result, "Should return the label-scope assignments from the assigner");
  }
}
