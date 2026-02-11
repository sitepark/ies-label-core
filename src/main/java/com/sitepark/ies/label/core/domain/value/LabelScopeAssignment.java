package com.sitepark.ies.label.core.domain.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.concurrent.Immutable;

@Immutable
public class LabelScopeAssignment {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<String, Set<String>> assignments = new HashMap<>();

  @SuppressWarnings("PMD.LawOfDemeter")
  LabelScopeAssignment(Builder builder) {
    builder.assignments.forEach((key, value) -> this.assignments.put(key, Set.copyOf(value)));
  }

  public List<String> labelIds() {
    return List.copyOf(this.assignments.keySet());
  }

  public List<String> scopes(String labelId) {
    Set<String> scopes = this.assignments.get(labelId);
    if (scopes == null) {
      return List.of();
    }
    return List.copyOf(scopes);
  }

  public List<String> scopes() {
    Set<String> allScopes = new TreeSet<>();
    for (Set<String> scopes : this.assignments.values()) {
      allScopes.addAll(scopes);
    }
    return List.copyOf(allScopes);
  }

  public boolean isEmpty() {
    return this.assignments.isEmpty();
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
    return (o instanceof LabelScopeAssignment that)
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
    private final Map<String, Set<String>> assignments = new HashMap<>();

    private Builder() {}

    @SuppressWarnings("PMD.LawOfDemeter")
    private Builder(LabelScopeAssignment labelScopeAssignment) {
      labelScopeAssignment.assignments.forEach(
          (key, value) -> this.assignments.put(key, new TreeSet<>(value)));
    }

    public LabelScopeAssignment.Builder assignments(String labelId, List<String> scopes) {
      Objects.requireNonNull(scopes, "scopes must not be null");
      for (String scope : scopes) {
        this.assignment(labelId, scope);
      }
      return this;
    }

    public LabelScopeAssignment.Builder assignments(String labelId, String... scopes) {
      Objects.requireNonNull(scopes, "scopes must not be null");
      for (String scope : scopes) {
        this.assignment(labelId, scope);
      }
      return this;
    }

    public LabelScopeAssignment.Builder assignment(String labelId, String scope) {
      Objects.requireNonNull(labelId, "labelId must not be null");
      Objects.requireNonNull(scope, "scope must not be null");

      Set<String> scopes = this.assignments.computeIfAbsent(labelId, k -> new TreeSet<>());
      scopes.add(scope);
      return this;
    }

    public LabelScopeAssignment build() {
      return new LabelScopeAssignment(this);
    }
  }
}
