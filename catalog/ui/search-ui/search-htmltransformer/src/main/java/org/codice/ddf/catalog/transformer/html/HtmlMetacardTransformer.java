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

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.IfHelper;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.MetacardTransformer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HtmlMetacardTransformer extends HtmlTransformer implements MetacardTransformer {

  public HtmlMetacardTransformer() {
    handlebars = new Handlebars(templateLoader);
    handlebars.registerHelpers(new RecordViewHelpers());

    handlebars.registerHelper(
        "isMetacard",
        new IfHelper() {
          @Override
          public CharSequence apply(Object context, Options options) throws IOException {
            return (context instanceof Metacard) ? options.fn() : options.inverse();
          }
        });

    resolvers =
        new ValueResolver[] {
          new MetacardValueResolver(), MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE
        };
    try {
      handlebars.compile(RECORD_TEMPLATE);
      template = handlebars.compile(RECORD_HTML_TEMPLATE);
    } catch (IOException e) {
      LOGGER.debug("Failed to load templates", e);
    }
  }

  @Override
  public BinaryContent transform(Metacard metacard, Map<String, Serializable> arguments)
      throws CatalogTransformerException {

    if (metacard == null) {
      throw new CatalogTransformerException("Cannot transform null metacard.");
    }

    String html = buildHtml(metacard);

    if (html != null) {
      return new BinaryContentImpl(
          new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), DEFAULT_MIME_TYPE);
    } else {
      throw new CatalogTransformerException("No content.");
    }
  }

  String buildHtml(Metacard metacard) {

    try {
      Context context = Context.newBuilder(metacard).resolver(resolvers).build();
      return template.apply(context);
    } catch (IOException e) {
      LOGGER.debug("Failed to apply template", e);
    }

    return null;
  }
}
