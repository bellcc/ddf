package ddf.catalog.transformer.html;

import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.QueryResponseTransformer;
import ddf.catalog.transformer.html.models.HtmlMetacardModel;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HtmlResultSetTransformer extends HtmlMetacard implements QueryResponseTransformer {

  @Override
  public BinaryContent transform(SourceResponse sourceResponse, Map<String, Serializable> map)
      throws CatalogTransformerException {

    if (sourceResponse == null) {
      throw new CatalogTransformerException("Null result set cannot be transformed to HTML");
    }

    List<Metacard> metacards =
        sourceResponse.getResults()
            .stream()
            .map(Result::getMetacard)
            .collect(Collectors.toList());

    List<HtmlMetacardModel> metacardModels = new ArrayList<>();

    for (Metacard metacard : metacards) {
      metacardModels.add(new HtmlMetacardModel(metacard, new ArrayList<>()));
    }

    String html = buildHtml(metacardModels);

    if (html == null) {
      throw new CatalogTransformerException("Result set cannot be transformed to HTML");
    } else {
      return new BinaryContentImpl(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
    }
  }

}
