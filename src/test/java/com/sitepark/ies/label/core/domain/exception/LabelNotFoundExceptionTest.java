package com.sitepark.ies.label.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LabelNotFoundExceptionTest {

  @Test
  void testGetId() {
    LabelNotFoundException exception = new LabelNotFoundException("42");
    assertEquals(
        "42", exception.getId(), "getId() should return the label ID passed to the constructor");
  }

  @Test
  void testGetMessage() {
    LabelNotFoundException exception = new LabelNotFoundException("42");
    assertEquals(
        "Label with id 42 not found",
        exception.getMessage(),
        "getMessage() should include the label ID in the error message");
  }
}
