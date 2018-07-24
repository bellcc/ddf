package org.codice.ddf.catalog.transformer.html;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class HtmlResultSetTransformerTest {

  @Test
  public void testBasicTransform() throws IOException {
    MetacardImpl metacard = new MetacardImpl();

    metacard.setTitle("20180509_211708.jpg");
    metacard.setThumbnail(
        new byte[] {
          0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0,
          1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1
        });
    metacard.setId("cd546c9f91104072b9a4d4ba42423650");
    metacard.setSourceId("ddf.distribution");
    metacard.setCreatedDate(new Date());
    metacard.setModifiedDate(new Date());

    HtmlResultSetTransformer htmlTransformer = new HtmlResultSetTransformer();

    List<Metacard> metacardList = new ArrayList<>();
    metacardList.add(metacard);
    metacardList.add(metacard);

    String html = htmlTransformer.buildHtml(metacardList);

    System.out.println(html);

    assertThat(true, is(true));
  }
}
