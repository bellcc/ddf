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
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.QueryResponseTransformer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HtmlResultSetTransformer extends HtmlTransformer implements QueryResponseTransformer {

  public HtmlResultSetTransformer() {
    templateLoader = new ClassPathTemplateLoader();
    templateLoader.setPrefix("/templates");
    templateLoader.setSuffix(".hbt");

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
      handlebars.compile("recordContents");
      template = handlebars.compile("recordHtml");
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  public BinaryContent transform(
      SourceResponse upstreamResponse, Map<String, Serializable> arguments)
      throws CatalogTransformerException {

    List<Metacard> metacards =
        upstreamResponse
            .getResults()
            .stream()
            .map(Result::getMetacard)
            .collect(Collectors.toList());

    String html = buildHtml(metacards);

    return new BinaryContentImpl(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
  }

  public String buildHtml(List<Metacard> metacardList) {

    try {
      Context context = Context.newBuilder(metacardList).resolver(resolvers).build();
      return template.apply(context);
    } catch (IOException e) {

    }

    return null;
  }
}
