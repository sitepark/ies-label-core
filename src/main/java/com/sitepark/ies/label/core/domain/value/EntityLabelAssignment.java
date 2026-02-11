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
public class EntityLabelAssignment {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<EntityRef, Set<String>> assignments = new HashMap<>();

  @SuppressWarnings("PMD.LawOfDemeter")
  EntityLabelAssignment(Builder builder) {
    builder.assignments.forEach((key, value) -> this.assignments.put(key, Set.copyOf(value)));
  }

  public List<EntityRef> entityRefs() {
    return List.copyOf(this.assignments.keySet());
  }

  public List<String> labelIds(EntityRef entityRef) {
    Set<String> labelIds = this.assignments.get(entityRef);
    if (labelIds == null) {
      return List.of();
    }
    return List.copyOf(labelIds);
  }

  public List<String> labelIds() {
    Set<String> allLabelIds = new TreeSet<>();
    for (Set<String> labelIds : this.assignments.values()) {
      allLabelIds.addAll(labelIds);
    }
    return List.copyOf(allLabelIds);
  }

  public boolean isEmpty() {
    return this.assignments.isEmpty();
  }

  public int countAssignments() {
    return this.assignments.values().stream().mapToInt(Set::size).sum();
  }

  public int size() {
    return this.assignments.size();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.assignments);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof EntityLabelAssignment that)
        && Objects.equals(this.assignments, that.assignments);
  }

  @Override
  public String toString() {
    return "EntitiesLabelsAssignment{" + "assignments=" + assignments + '}';
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<EntityRef, Set<String>> assignments = new HashMap<>();

    private Builder() {}

    @SuppressWarnings("PMD.LawOfDemeter")
    private Builder(EntityLabelAssignment entitiesLabelsAssignment) {
      entitiesLabelsAssignment.assignments.forEach(
          (key, value) -> this.assignments.put(key, new TreeSet<>(value)));
    }

    public Builder assignments(EntityRef entityRef, List<String> labelIds) {
      Objects.requireNonNull(labelIds, "labelIds must not be null");
      for (String labelId : labelIds) {
        this.assignment(entityRef, labelId);
      }
      return this;
    }

    public Builder assignments(EntityRef entityRef, String... labelIds) {
      Objects.requireNonNull(labelIds, "labelIds must not be null");
      for (String labelId : labelIds) {
        this.assignment(entityRef, labelId);
      }
      return this;
    }

    public Builder assignment(EntityRef entityRef, String labelId) {
      Objects.requireNonNull(entityRef, "entityRef must not be null");
      Objects.requireNonNull(labelId, "labelId must not be null");

      Set<String> userIds = this.assignments.computeIfAbsent(entityRef, k -> new TreeSet<>());
      userIds.add(labelId);
      return this;
    }

    public EntityLabelAssignment build() {
      return new EntityLabelAssignment(this);
    }
  }
}
