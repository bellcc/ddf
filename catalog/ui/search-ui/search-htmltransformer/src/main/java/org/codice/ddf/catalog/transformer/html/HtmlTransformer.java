package org.codice.ddf.catalog.transformer.html;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class HtmlTransformer {

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
}
