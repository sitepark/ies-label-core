package com.sitepark.ies.label.core.port;

import com.sitepark.ies.label.core.domain.entity.Label;
import java.util.List;
import java.util.Optional;

public interface LabelRepository extends AnchorResolver {

  String create(Label label);

  void update(Label label);

  void remove(String id);

  void restore(Label label);

  Optional<Label> get(String id);

  List<Label> search(String term, List<String> scopes);

  List<Label> getAllLabels();

  List<Label> getByIds(List<String> ids);
}
