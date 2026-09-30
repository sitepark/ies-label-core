package com.sitepark.ies.label.core.api;

import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface LabelsQuery {
  List<String> getEntityIdsByLabels(String entityType, List<String> labelIds);
}
