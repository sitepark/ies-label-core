package com.sitepark.ies.label.core.port;

import com.sitepark.ies.label.core.domain.value.LabelScopeAssignment;
import java.util.List;

public interface LabelScopeAssigner {

  void assignScopesToLabels(List<String> labelIds, List<String> scopeIds);

  void reassignScopesToLabels(List<String> labelIds, List<String> scopeIds);

  void unassignScopesFromLabels(List<String> labelIds, List<String> scopeIds);

  void unassignAllLabelsFromScopes(List<String> labelIds);

  void unassignAllScopesFromLabel(List<String> scopeIds);

  List<String> getScopesAssignByLabel(String labelId);

  List<String> getLabelsAssignByScope(String scopeId);

  LabelScopeAssignment getScopesAssignByLabels(List<String> labelIds);

  List<String> getAllScopes();
}
