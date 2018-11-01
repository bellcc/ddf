/*
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.catalog.ui.query.transform;

import com.google.common.collect.ImmutableMap;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.AttributeImpl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl;

public class QuerySortsValueTransformer implements QueryValueTransformer {

  @Override
  public boolean filter(String key, Object value) {
    return QueryMetacardTypeImpl.QUERY_SORTS.equals(key);
  }

  @Override
  public Attribute createAttribute(String key, Object value) {
    List<Serializable> values =
        ((List<Object>) value)
            .stream()
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(this::serializeField)
            .collect(Collectors.toList());

    return new AttributeImpl(key, values);
  }

  @Override
  public Object getJsonValue(String key, Object value) {
    return ((List<Object>) value)
        .stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .map(this::deserializeField)
        .collect(Collectors.toList());
  }

  @Override
  public Priority getPriority() {
    return Priority.FIELD;
  }

  @Override
  public int compareTo(QueryValueTransformer o) {
    return this.getPriority().ordinal() - o.getPriority().ordinal();
  }

  private String serializeField(Map<String, String> sorts) {
    return String.format(
        "%s,%s", sorts.getOrDefault("attribute", ""), sorts.getOrDefault("direction", ""));
  }

  private Map<String, String> deserializeField(String sorts) {
    final String[] sortParameters = sorts.split(",");
    return ImmutableMap.<String, String>builder()
        .put("attribute", sortParameters[0])
        .put("direction", sortParameters[1])
        .build();
  }
}
