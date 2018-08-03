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
