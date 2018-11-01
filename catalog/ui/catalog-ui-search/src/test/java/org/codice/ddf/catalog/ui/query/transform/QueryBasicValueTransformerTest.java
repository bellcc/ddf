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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.types.Core;
import org.junit.Before;
import org.junit.Test;

public class QueryBasicValueTransformerTest {

  private QueryValueTransformer transformer;

  @Before
  public void setup() {
    this.transformer = new QueryBasicValueTransformer();
  }

  @Test
  public void testValidBasicValueFilter() {
    boolean result = transformer.canHandle(null, "");
    assertThat(result, is(true));
  }

  @Test
  public void testInvalidBasicValueFilter() {
    boolean result = transformer.canHandle(null, null);
    assertThat(result, is(false));
  }

  @Test
  public void testCreateAttribute() {
    Attribute attr = transformer.createAttribute(Core.ID, "Hello World");
    assertThat(attr.getName(), is(Core.ID));
    assertThat(attr.getValue(), is("Hello World"));
  }

  @Test
  public void testGetJsonValue() {
    Object value = transformer.getJsonValue(null, "Hello World");
    assertThat(value, is("Hello World"));
  }
}
