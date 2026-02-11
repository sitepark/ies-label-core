package com.sitepark.ies.label.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class UpdateLabelFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public UpdateLabelFailedException(String message) {
    super(message);
  }

  public UpdateLabelFailedException(String message, Throwable t) {
    super(message, t);
  }
}
