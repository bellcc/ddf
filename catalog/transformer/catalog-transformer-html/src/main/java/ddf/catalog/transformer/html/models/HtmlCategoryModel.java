/**
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
package ddf.catalog.transformer.html.models;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HtmlCategoryModel {

  private String title;
  private Map<String, Object> attributes;

  public HtmlCategoryModel(Metacard metacard, String title, List<String> attributeList) {
    this.title = title;

    // A tree map will alphabetize the attribute names
    this.attributes = new TreeMap<>();

    mapAttributes(metacard, attributeList);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public String getTitle() {
    return this.title;
  }

  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  private void mapAttributes(Metacard metacard, List<String> attributeList) {
    for (String attrKey : attributeList) {
      // TODO Figure out what the difference between getValue() is and getValues()
      // TODO Replace the key with a human readable attribute value

      Attribute attr = metacard.getAttribute(attrKey);

      if (attr == null) {
        this.attributes.put(attrKey, new HtmlEmptyValueModel());
      } else if (attrKey.equals("thumbnail")) {
        byte[] imageData = (byte[]) attr.getValue();
        this.attributes.put(attrKey, new HtmlMediaModel(imageData));
      } else {
        this.attributes.put(attrKey, new HtmlBasicValueModel(attr.getValue()));
      }
    }
  }
}
