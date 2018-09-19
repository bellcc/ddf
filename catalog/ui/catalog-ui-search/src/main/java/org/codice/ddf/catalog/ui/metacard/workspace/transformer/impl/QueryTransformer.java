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
package org.codice.ddf.catalog.ui.metacard.workspace.transformer.impl;

import com.google.common.collect.ImmutableMap;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardImpl;
import org.codice.ddf.catalog.ui.query.transform.QueryValueTransformer;
import org.codice.ddf.catalog.ui.util.EndpointUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that performs common functions for handling the input and output of query endpoints
 */
public class QueryTransformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueryTransformer.class);

  private EndpointUtil endpointUtil;

  private List<QueryValueTransformer> queryValueTransformers;

  private static final Map<String, String> FIELD_MAPPINGS = ImmutableMap.of("src", "sources");

  public QueryTransformer(
      EndpointUtil endpointUtil, List<QueryValueTransformer> queryValueTransformers) {
    this.endpointUtil = endpointUtil;
    this.queryValueTransformers = queryValueTransformers;

    Collections.sort(queryValueTransformers);
  }

  public Map<String, Object> transformMetacardIntoMap(Metacard metacard) {
    Map<String, Object> map = endpointUtil.getMetacardMap(metacard);

    return map.entrySet()
        .stream()
        .filter(entry -> Objects.nonNull(entry.getValue()))
        .filter(entry -> isBasicType(entry.getValue()))
        .map(
            entry -> {
              String key = entry.getKey();
              Object value = entry.getValue();

              QueryValueTransformer transformer = getQueryValueTransformer(key, value);

              if (transformer != null) {
                return new AbstractMap.SimpleEntry<>(key, transformer.getJsonValue(key, value));
              } else {
                LOGGER.trace(
                    "Appropriate query value transformer could not be found for [{}] with value [{}]",
                    key,
                    value);
              }

              return null;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  public Metacard transformJsonIntoMetacard(Map<String, Object> json) {
    QueryMetacardImpl query = new QueryMetacardImpl();

    json.forEach(
        (key, value) -> {
          QueryValueTransformer transformer = getQueryValueTransformer(key, value);

          if (transformer != null) {
            String attrKey = FIELD_MAPPINGS.getOrDefault(key, key);

            Attribute attr = transformer.createAttribute(attrKey, value);
            query.setAttribute(attr);
          } else {
            LOGGER.trace(
                "Appropriate query value transformer could not be found for [{}] with value [{}]",
                key,
                value);
          }
        });

    return query;
  }

  private boolean isBasicType(Object value) {
    return !(value instanceof List) || notEmptyList(value);
  }

  private boolean notEmptyList(Object value) {
    return value instanceof List && !((List) value).isEmpty();
  }

  private QueryValueTransformer getQueryValueTransformer(String key, Object value) {
    return queryValueTransformers
        .stream()
        .filter(transformer -> transformer.filter(key, value))
        .findFirst()
        .orElse(null);
  }

  public void setQueryValueTransformers(List<QueryValueTransformer> queryValueTransformers) {
    this.queryValueTransformers = queryValueTransformers;
    Collections.sort(queryValueTransformers);
  }

  public List<QueryValueTransformer> getQueryValueTransformers() {
    return this.queryValueTransformers;
  }
}
