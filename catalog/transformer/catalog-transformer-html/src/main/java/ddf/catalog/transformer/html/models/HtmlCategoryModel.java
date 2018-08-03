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
      // TODO Figure out what the difference between getValue() is and getValues() and when to use which
      // TODO Replace the key with a human readable attribute value

      Attribute attr = metacard.getAttribute(attrKey);

      if (attr == null) {
        this.attributes.put(attrKey, new HtmlEmptyValueModel());
      } else if (attrKey == "thumbnail") {
        byte[] imageData = (byte[]) attr.getValue();
        this.attributes.put(attrKey, new HtmlMediaModel(imageData));
      } else {
        this.attributes.put(attrKey, new HtmlBasicValueModel(attr.getValue()));
      }
    }
  }
}
