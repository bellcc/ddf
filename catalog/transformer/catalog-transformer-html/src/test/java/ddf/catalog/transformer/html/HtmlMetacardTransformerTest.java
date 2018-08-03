package ddf.catalog.transformer.html;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Associations;
import ddf.catalog.data.types.Core;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transformer.html.models.HtmlCategoryModel;
import ddf.catalog.transformer.html.models.HtmlMetacardModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

public class HtmlMetacardTransformerTest {

  private static final List<String> EMPTY_ATTRIBUTE_LIST = Collections.emptyList();
  private static final List<HtmlCategoryModel> EMPTY_CATEGORY_LIST = Collections.emptyList();

  private List<String> ASSOCIATIONS_LIST;
  private List<String> CORE_LIST;

  @Before
  public void setup() {
    ASSOCIATIONS_LIST = new ArrayList<>();
    ASSOCIATIONS_LIST.add(Associations.RELATED);
    ASSOCIATIONS_LIST.add(Associations.DERIVED);
    ASSOCIATIONS_LIST.add(Associations.EXTERNAL);

    CORE_LIST = new ArrayList<>();
    CORE_LIST.add(Core.THUMBNAIL);
  }

  @Test
  public void testMetacardCreation() {
    Metacard metacard = new MetacardImpl();

    List<HtmlMetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new HtmlMetacardModel(metacard, EMPTY_CATEGORY_LIST));
    metacardModelList.add(new HtmlMetacardModel(metacard, EMPTY_CATEGORY_LIST));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    assertThat(doc.select(".metacard").size(), is(metacardModelList.size()));
  }

  @Test
  public void testCategoryCreation() {
    Metacard metacard = new MetacardImpl();

    List<HtmlCategoryModel> categories = new ArrayList<>();
    categories.add(new HtmlCategoryModel(metacard, "Associations", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Contact", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Core", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "DateTime", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Location", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Media", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Security", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Topic", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Validation", EMPTY_ATTRIBUTE_LIST));
    categories.add(new HtmlCategoryModel(metacard, "Version", EMPTY_ATTRIBUTE_LIST));

    List<HtmlMetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new HtmlMetacardModel(metacard, categories));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    assertThat(doc.select(".metacard").size(), is(metacardModelList.size()));
    assertThat(doc.select(".metacard-category").size(), is(categories.size()));
  }

  @Test
  public void testAssociationsAttributes() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(Associations.RELATED, "");
    metacard.setAttribute(Associations.DERIVED, "");
    metacard.setAttribute(Associations.EXTERNAL, "");

    List<HtmlCategoryModel> categories = new ArrayList<>();
    categories.add(new HtmlCategoryModel(metacard, "Associations", ASSOCIATIONS_LIST));

    List<HtmlMetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new HtmlMetacardModel(metacard, categories));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    assertThat(doc.select(".metacard").size(), is(metacardModelList.size()));
    assertThat(doc.select(".metacard-category").size(), is(categories.size()));
    assertThat(doc.select(".category-table").size(), is(categories.size()));
    assertThat(doc.select(".metacard-attribute").size(), is(ASSOCIATIONS_LIST.size()));
  }

  @Test
  public void testEmptyAttributeValue() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(Associations.RELATED, null);
    metacard.setAttribute(Associations.DERIVED, "");
    metacard.setAttribute(Associations.EXTERNAL, "");

    List<HtmlCategoryModel> categories = new ArrayList<>();
    categories.add(new HtmlCategoryModel(metacard, "Associations", ASSOCIATIONS_LIST));

    List<HtmlMetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new HtmlMetacardModel(metacard, categories));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    assertThat(doc.select(".metacard-attribute").size(), is(ASSOCIATIONS_LIST.size()));
    assertThat(doc.select(".empty-attribute").size(), is(1));
  }

  @Test
  public void testMediaAttributeValue() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setThumbnail(new byte[] {});

    List<HtmlCategoryModel> categories = new ArrayList<>();
    categories.add(new HtmlCategoryModel(metacard, "Core", CORE_LIST));

    List<HtmlMetacardModel> metacardModelList = new ArrayList<>();
    metacardModelList.add(new HtmlMetacardModel(metacard, categories));

    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();

    Document doc = Jsoup.parse(htmlTransformer.buildHtml(metacardModelList));

    assertThat(doc.select(".media-attribute").size(), is(1));
  }

  @Test(expected = CatalogTransformerException.class)
  public void testNullMetacardTransform() throws CatalogTransformerException {
    HtmlMetacardTransformer htmlTransformer = new HtmlMetacardTransformer();
    htmlTransformer.transform(null, new HashMap<>());
  }
}
