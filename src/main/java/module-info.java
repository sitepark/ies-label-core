/**
 * This module contains the essential business logic and data structures, of the user repository.
 */
module com.sitepark.ies.label.core {
  exports com.sitepark.ies.label.core.domain.entity;
  exports com.sitepark.ies.label.core.domain.value;
  exports com.sitepark.ies.label.core.domain.exception;
  exports com.sitepark.ies.label.core.api;
  exports com.sitepark.ies.label.core.usecase;
  exports com.sitepark.ies.label.core.port;

  requires jakarta.inject;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.sitepark.ies.sharedkernel;
  requires org.apache.logging.log4j;
  requires static com.github.spotbugs.annotations;
  requires com.fasterxml.jackson.annotation;
  requires static org.jetbrains.annotations;
  requires com.fasterxml.jackson.databind;
  requires jsr305;

  opens com.sitepark.ies.label.core.domain.entity;
  opens com.sitepark.ies.label.core.usecase;
  opens com.sitepark.ies.label.core.domain.value;
}
