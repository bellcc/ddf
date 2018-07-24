package org.codice.ddf.catalog.resultset.html;

import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.MetacardTransformer;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class ResultSetHtmlTransformer implements MetacardTransformer {

  private static final MimeType DEFAULT_MIME_TYPE;

  static {
    MimeType mimeType = null;

    try {
      mimeType = new MimeType("text/html");
    } catch (MimeTypeParseException e) {
      e.printStackTrace();
    }

    DEFAULT_MIME_TYPE = mimeType;
  }

  @Override
  public BinaryContent transform(Metacard metacard, Map<String, Serializable> arguments)
      throws CatalogTransformerException {

    String html = "<html><body><h1>Hello World</h1></body></html>";

    return new BinaryContentImpl(
        new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), DEFAULT_MIME_TYPE);
  }

  private String buildHtml() {
    return "";
  }
}
