package com.sitepark.ies.label.core.port;

import com.sitepark.ies.label.core.domain.value.EntityLabelAssignment;
import com.sitepark.ies.label.core.domain.value.LabelEntityAssignment;
import com.sitepark.ies.sharedkernel.domain.EntityRef;
import java.util.List;

public interface LabelEntityAssigner {

  int assignEntitiesToLabels(List<String> labelIds, List<EntityRef> entityRefs);

  int unassignEntitiesFromLabels(List<String> labelIds, List<EntityRef> entityRefs);

  int unassignAllEntitiesFromLabels(List<String> labelIds);

  int unassignAllLabelsFromEntities(List<EntityRef> entityRefs);

  List<String> getEntitiesAssignByLabel(String labelId, String entityType);

  LabelEntityAssignment getEntitiesAssignByLabels(List<String> labelIds);

  EntityLabelAssignment getLabelsAssignByEntities(List<EntityRef> entityRefs);
}
