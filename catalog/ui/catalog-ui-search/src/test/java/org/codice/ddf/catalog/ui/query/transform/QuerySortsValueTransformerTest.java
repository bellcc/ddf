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

import static org.codice.ddf.catalog.ui.metacard.workspace.QueryMetacardTypeImpl.QUERY_SORTS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.types.Core;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class QuerySortsValueTransformerTest {

  private QueryValueTransformer transformer;

  @Before
  public void setup() {
    this.transformer = new QuerySortsValueTransformer();
  }

  @Test
  public void testValidSortsValueFilter() {
    boolean result = transformer.filter(QUERY_SORTS, null);
    assertThat(result, is(true));
  }

  @Test
  public void testInvalidSortsValueFilter() {
    boolean result = transformer.filter(Core.ID, null);
    assertThat(result, is(false));
  }

  @Test
  public void testCreateAttribute() {
    Map<String, String> basic = ImmutableMap.of("attribute", "modified", "direction", "descending");
    List<Map<String, String>> sorts = ImmutableList.of(basic, basic);

    Attribute attr = transformer.createAttribute(QUERY_SORTS, sorts);

    assertThat(attr.getName(), is(QUERY_SORTS));
    assertThat(attr.getValues(), is(Arrays.asList("modified,descending", "modified,descending")));
  }

  @Test
  public void testGetJsonValue() {
    Object result =
        transformer.getJsonValue(null, Arrays.asList("modified,descending", "modified,descending"));

    Map<String, String> basic = ImmutableMap.of("attribute", "modified", "direction", "descending");
    List<Map<String, String>> sorts = ImmutableList.of(basic, basic);

    assertThat(result, is(sorts));
  }
}
