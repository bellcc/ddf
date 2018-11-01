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

import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.AttributeImpl;
import java.io.Serializable;

public class QueryBasicValueTransformer implements QueryValueTransformer {

  @Override
  public boolean filter(String key, Object value) {
    return value instanceof Serializable;
  }

  @Override
  public Attribute createAttribute(String key, Object value) {
    return new AttributeImpl(key, (Serializable) value);
  }

  @Override
  public Object getJsonValue(String key, Object value) {
    return value;
  }

  @Override
  public Priority getPriority() {
    return Priority.GENERAL;
  }

  @Override
  public int compareTo(QueryValueTransformer o) {
    return this.getPriority().ordinal() - o.getPriority().ordinal();
  }
}
