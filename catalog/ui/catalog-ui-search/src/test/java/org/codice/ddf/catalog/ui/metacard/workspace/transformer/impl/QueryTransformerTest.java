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

import static org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl.QUERY_CQL;
import static org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl.QUERY_FEDERATION;
import static org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl.QUERY_FILTER_TREE;
import static org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl.QUERY_SORTS;
import static org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl.QUERY_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.types.Core;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.codice.ddf.catalog.ui.query.transform.QueryBasicValueTransformer;
import org.codice.ddf.catalog.ui.query.transform.QueryListValueTransformer;
import org.codice.ddf.catalog.ui.query.transform.QuerySortsValueTransformer;
import org.codice.ddf.catalog.ui.query.transform.QueryValueTransformer;
import org.codice.ddf.catalog.ui.util.EndpointUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueryTransformerTest {

  @Mock private Metacard metacard;

  @Mock private EndpointUtil endpointUtil;

  private QueryTransformer queryTransformer;

  private static final List<QueryValueTransformer> QUERY_VALUE_TRANSFORMERS =
      Arrays.asList(
          new QuerySortsValueTransformer(),
          new QueryBasicValueTransformer(),
          new QueryListValueTransformer());

  private Gson gson;

  private static final Type TYPE = new TypeToken<Map<String, Object>>() {}.getType();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.endpointUtil = mock(EndpointUtil.class);

    this.gson = new Gson();
    this.queryTransformer = new QueryTransformer(endpointUtil, QUERY_VALUE_TRANSFORMERS);
  }

  @Test
  public void testTransformMetacardIntoMap() {
    Map<String, Object> map = ImmutableMap.of(Core.TITLE, "title", Core.ID, "queryId");
    doReturn(map).when(endpointUtil).getMetacardMap(any(Metacard.class));

    Map<String, Object> metacardMap = queryTransformer.transformMetacardIntoMap(metacard);

    assertThat(metacardMap, is(map));
  }

  @Test
  public void testFilteredTransformMetacardIntoMap() {
    Map<String, Object> map =
        ImmutableMap.of(
            Core.TITLE, "title", Core.ID, "", Core.METACARD_TAGS, Collections.emptyList());
    doReturn(map).when(endpointUtil).getMetacardMap(any(Metacard.class));

    Map<String, Object> metacardMap = queryTransformer.transformMetacardIntoMap(metacard);

    assertThat(metacardMap, hasKey(Core.TITLE));
    assertThat(metacardMap, hasKey(Core.ID));
    assertThat(metacardMap.keySet(), hasSize(2));
  }

  @Test
  public void testTransformJsonIntoMetacard() throws IOException {
    InputStream resourceStream =
        QueryTransformerTest.class.getResourceAsStream("/sample_query.json");
    String contents = IOUtils.toString(resourceStream, "UTF-8");

    Map<String, Object> queryMap = gson.fromJson(contents, TYPE);
    Metacard query = queryTransformer.transformJsonIntoMetacard(queryMap);

    assertThat(query.getAttribute(QUERY_CQL).getValue(), is("(\"anyText\" ILIKE '%')"));
    assertThat(
        query.getAttribute(QUERY_FILTER_TREE).getValue(),
        is("{\"property\":\"anyText\",\"value\":\"\",\"type\":\"ILIKE\"}"));
    assertThat(query.getAttribute(Core.TITLE).getValue(), is("Title"));
    assertThat(query.getAttribute("excludeUnnecessaryAttributes").getValue(), is(true));
    assertThat(query.getAttribute("count").getValue(), is(250.0));
    assertThat(query.getAttribute("start").getValue(), is(1.0));
    assertThat(query.getAttribute(QUERY_FEDERATION).getValue(), is("enterprise"));
    assertThat(query.getAttribute(QUERY_SORTS).getValues(), is(getSortsField()));
    assertThat(query.getAttribute("serverPageIndex").getValue(), is(0.0));
    assertThat(query.getAttribute(QUERY_TYPE).getValue(), is("basic"));
    assertThat(query.getAttribute("isLocal").getValue(), is(false));
    assertThat(query.getAttribute("isOutdated").getValue(), is(true));
    assertThat(query.getId(), is("queryId"));
    assertThat(query.getAttribute("color").getValue(), is("#24ff24"));
  }

  @Test
  public void testSortQueryValueTransformers() {
    QueryValueTransformer sortsValueTransformer = new QuerySortsValueTransformer();

    List<QueryValueTransformer> transformers =
        Arrays.asList(
            new QueryBasicValueTransformer(),
            new QueryListValueTransformer(),
            sortsValueTransformer);

    Collections.sort(transformers);

    assertThat(transformers.get(0), is(sortsValueTransformer));
  }

  private List<String> getSortsField() {
    return Arrays.asList(
        "modified,descending", "associations.external,descending", "RELEVANCE,descending");
  }
}
