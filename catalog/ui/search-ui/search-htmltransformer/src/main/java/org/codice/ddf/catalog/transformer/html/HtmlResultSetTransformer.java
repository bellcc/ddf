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
package org.codice.ddf.catalog.transformer.html;

import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.QueryResponseTransformer;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HtmlResultSetTransformer extends HtmlTransformer implements QueryResponseTransformer {

  @Override
  public BinaryContent transform(
      SourceResponse upstreamResponse, Map<String, Serializable> arguments)
      throws CatalogTransformerException {

    if (upstreamResponse == null) {
      throw new CatalogTransformerException(
          "Cannot transform null source response for metacard result set");
    }

    List<Metacard> metacards =
        upstreamResponse
            .getResults()
            .stream()
            .map(Result::getMetacard)
            .collect(Collectors.toList());

    String html = buildHtml(metacards);

    if (html != null) {
      return new BinaryContentImpl(
          new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), DEFAULT_MIME_TYPE);
    } else {
      throw new CatalogTransformerException("No content available");
    }
  }
}
