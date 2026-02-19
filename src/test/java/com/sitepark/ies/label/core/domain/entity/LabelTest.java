package com.sitepark.ies.label.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_NONVIRTUAL", "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"})
class LabelTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(Label.class)
        .withPrefabValues(Anchor.class, Anchor.ofString("anchor-a"), Anchor.ofString("anchor-b"))
        .verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(Label.class).verify();
  }

  @Test
  void testBuildId() {
    Label label = Label.builder().id("1").build();
    assertEquals("1", label.id(), "Unexpected id");
  }

  @Test
  void testBuildName() {
    Label label = Label.builder().name("Test Label").build();
    assertEquals("Test Label", label.name(), "Unexpected name");
  }

  @Test
  void testBuildAnchorFromString() {
    Label label = Label.builder().anchor("my-label").build();
    assertEquals(Anchor.ofString("my-label"), label.anchor(), "Unexpected anchor");
  }

  @Test
  void testBuildAnchorFromAnchorObject() {
    Anchor anchor = Anchor.ofString("my-label");
    Label label = Label.builder().anchor(anchor).build();
    assertEquals(anchor, label.anchor(), "Unexpected anchor");
  }

  @Test
  void testBuildColorNormalizesToLowercase() {
    Label label = Label.builder().color("FF0000").build();
    assertEquals("ff0000", label.color(), "Color should be normalized to lowercase");
  }

  @Test
  void testBuildColorWithNull() {
    Label label = Label.builder().color(null).build();
    assertNull(label.color(), "Null color should be stored as null");
  }

  @Test
  void testBuildColorWithInvalidFormatThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Label.builder().color("ZZZZZZ"),
        "Invalid hex color should throw IllegalArgumentException");
  }

  @Test
  void testBuildDescription() {
    Label label = Label.builder().description("A test label").build();
    assertEquals("A test label", label.description(), "Unexpected description");
  }

  @Test
  void testToBuilderCreatesEqualCopyWithChangedField() {
    Label original = Label.builder().id("1").name("Original").color("ff0000").build();
    Label copy = original.toBuilder().name("Updated").build();
    Label expected = Label.builder().id("1").name("Updated").color("ff0000").build();
    assertEquals(
        expected, copy, "toBuilder should create a copy with only the specified field changed");
  }

  @Test
  void testToIdentifierReturnsIdWhenIdIsSet() {
    Label label = Label.builder().id("1").build();
    assertEquals(
        Identifier.ofId("1"),
        label.toIdentifier(),
        "toIdentifier should return ID-based Identifier when ID is set");
  }

  @Test
  void testToIdentifierReturnsAnchorWhenOnlyAnchorIsSet() {
    Label label = Label.builder().anchor("my-label").build();
    assertEquals(
        Identifier.ofAnchor(Anchor.ofString("my-label")),
        label.toIdentifier(),
        "toIdentifier should return Anchor-based Identifier when only anchor is set");
  }

  @Test
  void testToIdentifierPrefersIdOverAnchor() {
    Label label = Label.builder().id("1").anchor("my-label").build();
    assertEquals(
        Identifier.ofId("1"),
        label.toIdentifier(),
        "toIdentifier should prefer ID over anchor when both are set");
  }

  @Test
  void testToIdentifierReturnsNullWhenNeitherIdNorAnchorIsSet() {
    Label label = Label.builder().name("No Id Label").build();
    assertNull(
        label.toIdentifier(), "toIdentifier should return null when neither ID nor anchor is set");
  }

  @Test
  void testSerialize() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    Label label = Label.builder().id("1").name("Test").color("ff0000").description("desc").build();
    String json = mapper.writeValueAsString(label);
    assertEquals(
        "{\"id\":\"1\",\"name\":\"Test\",\"color\":\"ff0000\",\"description\":\"desc\"}",
        json,
        "Unexpected JSON serialization output");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"id\":\"1\",\"name\":\"Test\",\"color\":\"ff0000\",\"description\":\"desc\"}";
    Label label = mapper.readValue(json, Label.class);
    Label expected =
        Label.builder().id("1").name("Test").color("ff0000").description("desc").build();
    assertEquals(expected, label, "Deserialized label should equal the expected label");
  }
}
