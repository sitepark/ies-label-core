package com.sitepark.ies.label.core.usecase;

import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Request to remove a single label from the repository.
 *
 * @param identifier the identifier (ID or anchor) of the label to remove
 */
public record RemoveLabelRequest(@NotNull Identifier identifier) {

  /**
   * Creates a new builder for RemoveLabelRequest.
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for RemoveLabelRequest. */
  public static final class Builder {

    private Identifier identifier;

    /**
     * Sets the identifier for the label to remove.
     *
     * @param identifier the label identifier (ID or anchor)
     * @return this builder
     */
    public Builder identifier(Identifier identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * Sets the label ID to remove.
     *
     * @param id the label ID
     * @return this builder
     */
    public Builder id(String id) {
      this.identifier = Identifier.ofId(id);
      return this;
    }

    /**
     * Sets the label anchor to remove.
     *
     * @param anchor the label anchor
     * @return this builder
     */
    public Builder anchor(String anchor) {
      this.identifier = Identifier.ofAnchor(anchor);
      return this;
    }

    /**
     * Builds the RemoveLabelRequest.
     *
     * @return the request instance
     * @throws NullPointerException if identifier is null
     */
    public RemoveLabelRequest build() {
      Objects.requireNonNull(this.identifier, "identifier must not be null");
      return new RemoveLabelRequest(this.identifier);
    }
  }
}
