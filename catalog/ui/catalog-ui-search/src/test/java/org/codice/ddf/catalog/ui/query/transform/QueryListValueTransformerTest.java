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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import ddf.catalog.data.Attribute;
import java.util.Arrays;
import java.util.Collections;
import org.boon.core.value.ValueList;
import org.junit.Before;
import org.junit.Test;

public class QueryListValueTransformerTest {

  private QueryValueTransformer transformer;

  @Before
  public void setup() {
    this.transformer = new QueryListValueTransformer();
  }

  @Test
  public void testValidListValueFilter() {
    boolean result = this.transformer.filter(null, new ValueList(true));
    assertThat(result, is(true));
  }

  @Test
  public void testInvalidListValueFilter() {
    boolean result = this.transformer.filter(null, Collections.emptyList());
    assertThat(result, is(false));
  }

  @Test
  public void testCreateAttribute() {
    ValueList list = new ValueList(true);
    list.add("Hello");
    list.add("World");

    Attribute attr = transformer.createAttribute("items", list);

    assertThat(attr.getName(), is("items"));
    assertThat(attr.getValues(), is(Arrays.asList("Hello", "World")));
  }

  @Test
  public void testGetJsonValue() {
    Object values = transformer.getJsonValue(null, Arrays.asList("foo", "bar"));
    assertThat(values, is(Arrays.asList("foo", "bar")));
  }
}
