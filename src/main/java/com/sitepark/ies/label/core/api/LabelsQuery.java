package com.sitepark.ies.label.core.api;

import java.util.List;

public interface LabelsQuery {
  List<String> getEntityIdsByLabels(String entityType, List<String> labelIds);
}
