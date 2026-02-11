package com.sitepark.ies.label.core.usecase;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.sharedkernel.base.ListBuilder;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = UnassignEntitiesFromLabelsRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class UnassignEntitiesFromLabelsRequest {

  @NotNull private final List<EntityRef> entityRefs;

  @NotNull private final List<Identifier> labelIdentifiers;

  @Nullable private final String auditParentId;

  private UnassignEntitiesFromLabelsRequest(Builder builder) {
    this.entityRefs = List.copyOf(builder.entityRefs);
    this.labelIdentifiers = List.copyOf(builder.labelIdentifiers);
    this.auditParentId = builder.auditParentId;
  }

  public boolean isEmpty() {
    return this.entityRefs.isEmpty() || this.labelIdentifiers.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<EntityRef> entityRefs() {
    return this.entityRefs;
  }

  public List<Identifier> labelIdentifiers() {
    return this.labelIdentifiers;
  }

  public String auditParentId() {
    return this.auditParentId;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.labelIdentifiers, this.entityRefs, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UnassignEntitiesFromLabelsRequest that)
        && Objects.equals(this.entityRefs, that.entityRefs)
        && Objects.equals(this.labelIdentifiers, that.labelIdentifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "UnassignEntitiesFromLabelsRequest{"
        + ", entityRefs="
        + entityRefs
        + "labelIdentifiers="
        + labelIdentifiers
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<EntityRef> entityRefs = new TreeSet<>();
    private final Set<Identifier> labelIdentifiers = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(UnassignEntitiesFromLabelsRequest request) {
      this.entityRefs.addAll(request.entityRefs);
      this.labelIdentifiers.addAll(request.labelIdentifiers);
      this.auditParentId = request.auditParentId;
    }

    public Builder entityRefs(Consumer<ListBuilder<EntityRef>> configurer) {
      ListBuilder<EntityRef> listBuilder = new ListBuilder<>();
      configurer.accept(listBuilder);
      this.entityRefs.clear();
      this.entityRefs.addAll(listBuilder.build());
      return this;
    }

    public Builder labelIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.labelIdentifiers.clear();
      this.labelIdentifiers.addAll(listBuilder.build());
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public UnassignEntitiesFromLabelsRequest build() {
      return new UnassignEntitiesFromLabelsRequest(this);
    }
  }
}
