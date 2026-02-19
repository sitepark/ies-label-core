package com.sitepark.ies.label.core.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.label.core.port.AnchorResolver;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IdentifierResolverTest {

  private AnchorResolver anchorResolver;

  @BeforeEach
  void setUp() {
    this.anchorResolver = mock();
  }

  @Test
  void testResolveIdReturnsId() {
    IdentifierResolver resolver = IdentifierResolver.create(this.anchorResolver);
    String result = resolver.resolve(Identifier.ofId("1"));
    assertEquals("1", result, "Resolving an ID identifier should return the ID string");
  }

  @Test
  void testResolveAnchorReturnsResolvedId() {
    when(this.anchorResolver.resolveAnchor(any())).thenReturn(Optional.of("42"));
    IdentifierResolver resolver = IdentifierResolver.create(this.anchorResolver);
    String result = resolver.resolve(Identifier.ofAnchor(Anchor.ofString("my-anchor")));
    assertEquals(
        "42", result, "Resolving an anchor identifier should return the resolved database ID");
  }

  @Test
  void testResolveAnchorThrowsWhenNotFound() {
    when(this.anchorResolver.resolveAnchor(any())).thenReturn(Optional.empty());
    IdentifierResolver resolver = IdentifierResolver.create(this.anchorResolver);
    Identifier identifier = Identifier.ofAnchor(Anchor.ofString("unknown-anchor"));
    assertThrows(
        AnchorNotFoundException.class,
        () -> resolver.resolve(identifier),
        "Resolving an unknown anchor should throw AnchorNotFoundException");
  }

  @Test
  void testResolveListReturnsAllIds() {
    IdentifierResolver resolver = IdentifierResolver.create(this.anchorResolver);
    List<String> result = resolver.resolve(List.of(Identifier.ofId("1"), Identifier.ofId("2")));
    assertEquals(
        List.of("1", "2"), result, "Resolving a list of ID identifiers should return all IDs");
  }

  @Test
  void testResolveListWithAnchorReturnsResolvedId() {
    when(this.anchorResolver.resolveAnchor(any())).thenReturn(Optional.of("99"));
    IdentifierResolver resolver = IdentifierResolver.create(this.anchorResolver);
    List<String> result =
        resolver.resolve(List.of(Identifier.ofAnchor(Anchor.ofString("my-anchor"))));
    assertEquals(
        List.of("99"), result, "Resolving a list with an anchor should return the resolved ID");
  }
}
