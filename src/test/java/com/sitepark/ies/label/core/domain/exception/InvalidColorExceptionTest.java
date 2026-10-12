package com.sitepark.ies.label.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvalidColorExceptionTest {

  @Test
  void testGetColor() {
    InvalidColorException exception = new InvalidColorException("zzz");
    assertEquals(
        "zzz",
        exception.getColor(),
        "getColor() should return the color passed to the constructor");
  }

  @Test
  void testGetMessage() {
    InvalidColorException exception = new InvalidColorException("zzz");
    assertEquals(
        "Invalid color: zzz",
        exception.getMessage(),
        "getMessage() should include the color in the error message");
  }
}
