package com.sitepark.ies.label.core.usecase;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.label.core.domain.entity.Label;
import com.sitepark.ies.sharedkernel.base.ListBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(builder = UpdateLabelRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpdateLabelRequest {

  @NotNull private final Label label;

  @NotNull private final List<String> scopes;

  private UpdateLabelRequest(Builder builder) {
    this.label = builder.label;
    this.scopes = List.copyOf(builder.scopes);
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Label label() {
    return this.label;
  }

  @NotNull
  public List<String> scopes() {
    return this.scopes;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.label, this.scopes);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UpdateLabelRequest that)
        && Objects.equals(this.label, that.label)
        && Objects.equals(this.scopes, that.scopes);
  }

  @Override
  public String toString() {
    return "CreateUserRequest{" + "label=" + label + ", scopes=" + scopes + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Label label;
    private final Set<String> scopes = new TreeSet<>();

    private Builder() {}

    private Builder(UpdateLabelRequest request) {
      this.label = request.label;
      this.scopes.addAll(request.scopes);
    }

    public Builder label(Label label) {
      this.label = label;
      return this;
    }

    public Builder scopes(Consumer<ListBuilder<String>> configurer) {
      ListBuilder<String> listBuilder = new ListBuilder<>();
      configurer.accept(listBuilder);
      this.scopes.clear();
      this.scopes.addAll(listBuilder.build());
      return this;
    }

    public UpdateLabelRequest build() {
      Objects.requireNonNull(this.label, "Label must not be null");
      return new UpdateLabelRequest(this);
    }
  }
}
