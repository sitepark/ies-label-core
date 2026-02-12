package com.sitepark.ies.label.core.domain.value;

import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.concurrent.Immutable;

@Immutable
public class LabelEntityAssignment {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<String, Set<EntityRef>> assignments = new HashMap<>();

  @SuppressWarnings("PMD.LawOfDemeter")
  LabelEntityAssignment(Builder builder) {
    builder.assignments.forEach((key, value) -> this.assignments.put(key, Set.copyOf(value)));
  }

  public List<String> labelIds() {
    return List.copyOf(this.assignments.keySet());
  }

  public List<EntityRef> entityRefs(String labelId) {
    Set<EntityRef> entityRefs = this.assignments.get(labelId);
    if (entityRefs == null) {
      return List.of();
    }
    return List.copyOf(entityRefs);
  }

  public List<EntityRef> entityRefs() {
    Set<EntityRef> allEntityRefs = new TreeSet<>();
    for (Set<EntityRef> entityRefs : this.assignments.values()) {
      allEntityRefs.addAll(entityRefs);
    }
    return List.copyOf(allEntityRefs);
  }

  public boolean isEmpty() {
    return this.assignments.isEmpty();
  }

  public int size() {
    return this.assignments.values().stream().mapToInt(Set::size).sum();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.assignments);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof LabelEntityAssignment that)
        && Objects.equals(this.assignments, that.assignments);
  }

  @Override
  public String toString() {
    return "LabelEntityAssignment{" + "assignments=" + assignments + '}';
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, Set<EntityRef>> assignments = new HashMap<>();

    private Builder() {}

    @SuppressWarnings("PMD.LawOfDemeter")
    private Builder(LabelEntityAssignment labelEntityAssignment) {
      labelEntityAssignment.assignments.forEach(
          (key, value) -> this.assignments.put(key, new TreeSet<>(value)));
    }

    public Builder assignments(String labelId, List<EntityRef> entityRefs) {
      Objects.requireNonNull(entityRefs, "entityRefs must not be null");
      for (EntityRef entityRef : entityRefs) {
        this.assignment(labelId, entityRef);
      }
      return this;
    }

    public Builder assignments(String labelId, EntityRef... entityRefs) {
      Objects.requireNonNull(entityRefs, "entityRefs must not be null");
      for (EntityRef entityRef : entityRefs) {
        this.assignment(labelId, entityRef);
      }
      return this;
    }

    public Builder assignment(String labelId, EntityRef entityRef) {
      Objects.requireNonNull(labelId, "labelId must not be null");
      Objects.requireNonNull(entityRef, "entityRef must not be null");

      Set<EntityRef> entityRefs = this.assignments.computeIfAbsent(labelId, k -> new TreeSet<>());
      entityRefs.add(entityRef);
      return this;
    }

    public LabelEntityAssignment build() {
      return new LabelEntityAssignment(this);
    }
  }
}
