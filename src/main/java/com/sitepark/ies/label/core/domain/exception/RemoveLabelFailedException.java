package com.sitepark.ies.label.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class RemoveLabelFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public RemoveLabelFailedException(String message) {
    super(message);
  }

  public RemoveLabelFailedException(String message, Throwable t) {
    super(message, t);
  }
}
