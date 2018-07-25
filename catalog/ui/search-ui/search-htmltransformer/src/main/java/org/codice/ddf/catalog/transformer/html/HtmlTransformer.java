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
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.IfHelper;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import ddf.catalog.data.Metacard;
import java.io.IOException;
import java.util.List;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlTransformer {

  static final MimeType DEFAULT_MIME_TYPE;

  static final Logger LOGGER = LoggerFactory.getLogger(HtmlMetacardTransformer.class);

  static final String TEMPLATE_DIRECTORY = "/templates";

  static final String TEMPLATE_SUFFIX = ".hbt";

  static final String RECORD_TEMPLATE = "recordContents";

  static final String RECORD_HTML_TEMPLATE = "recordHtml";

  static ClassPathTemplateLoader templateLoader;

  Handlebars handlebars;

  Template template;

  ValueResolver[] resolvers;

  static {
    MimeType mimeType = null;
    try {
      mimeType = new MimeType("text/html");
    } catch (MimeTypeParseException e) {
      LOGGER.info("Failed to parse mimeType", e);
    }
    DEFAULT_MIME_TYPE = mimeType;

    templateLoader = new ClassPathTemplateLoader();
    templateLoader.setPrefix(TEMPLATE_DIRECTORY);
    templateLoader.setSuffix(TEMPLATE_SUFFIX);
  }

  public HtmlTransformer() {
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

  public <T> String buildHtml(T model) {

    if (!(model instanceof Metacard || model instanceof List)) {
      return null;
    }

    try {
      Context context = Context.newBuilder(model).resolver(resolvers).build();
      return template.apply(context);
    } catch (IOException e) {
      LOGGER.debug("Failed to apply template", e);
    }

    return null;
  }
}
