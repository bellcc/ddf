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

import ddf.catalog.data.Metacard;
import java.util.List;

public class HtmlMetacardModel {

  private List<HtmlCategoryModel> categories;

  private Metacard metacard;

  public HtmlMetacardModel(Metacard metacard, List<HtmlCategoryModel> categories) {
    this.metacard = metacard;
    this.categories = categories;
  }

  public void setCategories(List<HtmlCategoryModel> categories) {
    this.categories = categories;
  }

  public List<HtmlCategoryModel> getCategories() {
    return this.categories;
  }

  public void addCategory(HtmlCategoryModel category) {
    if (contains(category.getTitle())) {
      // TODO Log that duplicate category entries are not allowed
      return;
    }

    this.categories.add(category);
  }

  public boolean contains(String title) {
    return false;
  }
}
