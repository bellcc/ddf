package org.codice.ddf.catalog.resultset.html;

import static org.junit.Assert.*;

import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Core;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.junit.Before;
import org.junit.Test;

public class ResultSetHtmlTransformerTest {

  private ResultSetHtmlTransformer htmlTransformer;

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

  @Before
  private void setup() {
    htmlTransformer = new ResultSetHtmlTransformer();
  }

  @Test
  private void testFullTransform() throws CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(Core.TITLE, "Hello World");

    String html = "<html><body><h1>Hello World</h1></body></html>";

    assertEquals(
        htmlTransformer.transform(metacard, new HashMap<>()),
        new BinaryContentImpl(
            new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), DEFAULT_MIME_TYPE));
  }
}
