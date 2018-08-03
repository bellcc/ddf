package ddf.catalog.transformer.html;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.transformer.html.models.CategoryModel;
import ddf.catalog.transformer.html.models.MetacardModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class HtmlMetacardTransformerTest {

  private static final List<String> EMPTY_ATTRIBUTE_LIST = Collections.emptyList();
  private static final List<CategoryModel> EMPTY_CATEGORY_LIST = Collections.emptyList();

  @Test
  public void testMetacardCreation() {
    Metacard metacard = new MetacardImpl();

    List<MetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new MetacardModel(metacard, EMPTY_CATEGORY_LIST));
    metacardModelList.add(new MetacardModel(metacard, EMPTY_CATEGORY_LIST));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    System.out.println(doc.html());

    assertThat(doc.select(".metacard").size(), is(metacardModelList.size()));
  }

  @Test
  public void testCategoryCreation() {
    Metacard metacard = new MetacardImpl();

    List<CategoryModel> categories = new ArrayList<>();
    categories.add(new CategoryModel(metacard, "Associations", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Contact", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Core", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"DateTime", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Location", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Media", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Security", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Topic", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Validation", EMPTY_ATTRIBUTE_LIST));
    categories.add(new CategoryModel(metacard,"Version", EMPTY_ATTRIBUTE_LIST));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    List<MetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new MetacardModel(metacard, categories));

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    System.out.println(doc.html());

    assertThat(doc.select(".metacard").size() ,is(metacardModelList.size()));
    assertThat(doc.select(".metacard-panel").size(), is(categories.size()));
  }

}
