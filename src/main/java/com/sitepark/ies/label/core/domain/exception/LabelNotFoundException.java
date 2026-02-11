package com.sitepark.ies.label.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

/**
 * The <code>UserNotFoundException</code> exception is thrown when a user cannot be found or does
 * not exist in the system, typically when attempting to access or manipulate user-related
 * information for a user that is not present.
 */
public class LabelNotFoundException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  private final String id;

  public LabelNotFoundException(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  @Override
  public String getMessage() {
    return "Label with id " + this.id + " not found";
  }
}
