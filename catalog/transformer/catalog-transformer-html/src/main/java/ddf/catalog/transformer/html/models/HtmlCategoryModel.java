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
import ddf.catalog.data.types.Core;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.WordUtils;

public class HtmlCategoryModel implements HtmlExportCategory {

  private String title;

  private List<String> attributes;

  private Map<String, ValueModel> attributeMappings = new TreeMap<>();

  private static final String EMPTY_ATTRIBUTE_TEMPLATE = "emptyAttribute";

  private static final String BASIC_ATTRIBUTE_TEMPLATE = "basicAttribute";

  private static final String MEDIA_ATTRIBUTE_TEMPLATE = "mediaAttribute";

  public HtmlCategoryModel() {
    this("", new ArrayList<>());
  }

  public HtmlCategoryModel(String title, List<String> attributes) {
    this.title = title;
    this.attributes = attributes;
  }

  public void init() {
    // Called from blueprint
  }

  public void destroy(int code) {
    // Called from blueprint
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }

  public List<String> getAttributes() {
    return this.attributes;
  }

  public void setAttributes(Map<String, ValueModel> attributes) {
    this.attributeMappings = attributes;
  }

  public Map<String, ValueModel> getAttributeMappings() {
    return this.attributeMappings;
  }

  public void applyAttributeMappings(Metacard metacard) {
    for (String attrKey : attributes) {
      String readableKey = getHumanReadableAttribute(attrKey);
      Attribute attr = metacard.getAttribute(attrKey);

      ValueModel model;

      if (attr == null) {
        model = new HtmlEmptyValueModel(EMPTY_ATTRIBUTE_TEMPLATE);
        this.attributeMappings.put(readableKey, model);
      } else if (attrKey.equals(Core.THUMBNAIL)) {
        byte[] imageData = (byte[]) attr.getValue();
        model = new HtmlMediaModel(MEDIA_ATTRIBUTE_TEMPLATE, imageData);
      } else {
        model = new HtmlBasicValueModel(BASIC_ATTRIBUTE_TEMPLATE, attr.getValue());
      }

      this.attributeMappings.put(readableKey, model);
    }
  }

  private String getHumanReadableAttribute(String attr) {
    String readableAttr = attr;

    int periodIndex = readableAttr.lastIndexOf('.');
    if (periodIndex != -1) {
      readableAttr = attr.substring(periodIndex + 1);
    }

    readableAttr = readableAttr.replaceAll("-", " ");

    return WordUtils.capitalizeFully(readableAttr);
  }
}