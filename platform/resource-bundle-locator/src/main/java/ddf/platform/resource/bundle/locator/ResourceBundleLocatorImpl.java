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
package ddf.platform.resource.bundle.locator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleLocatorImpl implements ResourceBundleLocator {

  private File resourceBundleBaseDir;

  @Override
  public ResourceBundle getBundle(String baseName) {
    return getBundle(baseName, Locale.getDefault());
  }

  @Override
  public ResourceBundle getBundle(String baseName, Locale locale) {
    File resourceBundleDir = resourceBundleBaseDir;
    if (resourceBundleDir == null) {
      resourceBundleDir = new File(String.format("%s/etc/i18n/", System.getProperty("ddf.home")));
    }

    try {
      URL[] urls = {resourceBundleDir.toURI().toURL()};

      try (URLClassLoader loader = new URLClassLoader(urls)) {
        return ResourceBundle.getBundle(baseName, locale, loader);
      }

    } catch (MalformedURLException e) {
      throw new MissingResourceException(
          "An error occurred while loading ResourceBundle: " + baseName + "," + locale.getCountry(),
          getClass().getName(),
          baseName);
    } catch (IOException e) {
      throw new MissingResourceException(
          "An error occurred while creating class loader to URL for ResourceBundle: "
              + baseName
              + ","
              + locale.getCountry(),
          getClass().getName(),
          baseName);
    }
  }

  public void setResourceBundleBaseDir(String resourceBundleBaseDir) {
    this.resourceBundleBaseDir = new File(resourceBundleBaseDir);
  }
}
