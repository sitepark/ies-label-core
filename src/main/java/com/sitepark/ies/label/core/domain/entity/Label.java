package com.sitepark.ies.label.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.concurrent.Immutable;
import org.jetbrains.annotations.Nullable;

@Immutable
@JsonDeserialize(builder = Label.Builder.class)
public final class Label {

  @Nullable private final String id;
  @Nullable private final Anchor anchor;
  private final String name;
  private final String color;
  private final String description;

  private Label(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.name = builder.name;
    this.color = builder.color;
    this.description = builder.description;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Nullable
  public Identifier toIdentifier() {
    if (this.id != null) {
      return Identifier.ofId(this.id);
    }
    if (this.anchor != null) {
      return Identifier.ofAnchor(this.anchor);
    }
    return null;
  }

  @JsonProperty
  public String id() {
    return id;
  }

  @JsonProperty
  public Anchor anchor() {
    return anchor;
  }

  @JsonProperty
  public String name() {
    return name;
  }

  @JsonProperty
  public String color() {
    return color;
  }

  @JsonProperty
  public String description() {
    return description;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, anchor, name, color, description);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Label other)) return false;
    return Objects.equals(id, other.id)
        && Objects.equals(anchor, other.anchor)
        && Objects.equals(name, other.name)
        && Objects.equals(color, other.color)
        && Objects.equals(description, other.description);
  }

  @Override
  public String toString() {
    return "Label{"
        + "id='"
        + id
        + '\''
        + ", anchor='"
        + anchor
        + '\''
        + ", name='"
        + name
        + '\''
        + ", color='"
        + color
        + '\''
        + ", description='"
        + description
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    private String id;
    private Anchor anchor;
    private String name;
    private String color;
    private String description;

    private final Pattern COLOR_VALIDATOR = Pattern.compile("^[0-9a-fA-F]{6}$");

    public Builder() {}

    public Builder(Label label) {
      this.id = label.id;
      this.name = label.name;
      this.color = label.color;
      this.description = label.description;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder anchor(String anchor) {
      this.anchor = Anchor.ofString(anchor);
      return this;
    }

    public Builder anchor(Anchor anchor) {
      this.anchor = anchor;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder color(String color) {
      if (color != null && !COLOR_VALIDATOR.matcher(color).matches()) {
        throw new IllegalArgumentException("Invalid color: " + color);
      }
      this.color = color != null ? color.toLowerCase(Locale.ROOT) : null;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Label build() {
      return new Label(this);
    }
  }
}
