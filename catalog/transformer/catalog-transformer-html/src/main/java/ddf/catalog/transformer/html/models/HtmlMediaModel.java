package ddf.catalog.transformer.html.models;

import java.util.Base64;

public class HtmlMediaModel {
  private String data;

  public HtmlMediaModel(byte[] rawData) {
    this.data = getEncodedThumbnail(rawData);
  }

  private String getEncodedThumbnail(byte[] rawData) {
    if (rawData == null) {
      return null;
    }

    byte[] encoded = Base64.getEncoder().encode(rawData);
    return new String(encoded);
  }
}
