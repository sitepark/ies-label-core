package com.sitepark.ies.label.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

/** Thrown when a label color is not a valid six digit hexadecimal color. */
public class InvalidColorException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  private final String color;

  public InvalidColorException(String color) {
    this.color = color;
  }

  public String getColor() {
    return this.color;
  }

  @Override
  public String getMessage() {
    return "Invalid color: " + this.color;
  }
}
