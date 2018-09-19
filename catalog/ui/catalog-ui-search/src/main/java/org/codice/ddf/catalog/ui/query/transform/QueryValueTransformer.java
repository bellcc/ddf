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

/**
 * <b> This code is experimental. While this interface is functional and tested, it may change or be
 * removed in a future version of the library. </b>
 */
public interface QueryValueTransformer extends Comparable<QueryValueTransformer> {

  enum Priority {
    FIELD,
    GENERAL
  }

  /**
   * Predicate that determines if a field should be transformed by this transformer
   *
   * @param key The key of the field
   * @param value The value of the field
   * @return True if this field should be handled by this transformer and false otherwise
   */
  boolean canHandle(String key, Object value);

  /**
   * Creates a metacard attribute for a given key and value
   *
   * @param key The key of the field
   * @param value The value of the field
   * @return A metacard attribute
   */
  Attribute createAttribute(String key, Object value);

  /**
   * Creates a generic object for a JSON response body
   *
   * @param key The attribute's key
   * @param value The value or values of the attribute
   * @return An object that can be transformed to JSON
   */
  Object getJsonValue(String key, Object value);

  /** Returns the priority of a given query value transformer. */
  Priority getPriority();
}
