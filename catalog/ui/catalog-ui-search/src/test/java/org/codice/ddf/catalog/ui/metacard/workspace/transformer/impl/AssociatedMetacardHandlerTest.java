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
package org.codice.ddf.catalog.ui.metacard.workspace.transformer.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Associations;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.impl.CreateResponseImpl;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AssociatedMetacardHandlerTest {

  @Mock private CatalogFramework catalogFramework;

  private AssociatedMetacardHandler handler;

  @Before
  public void setup() {
    handler = new AssociatedMetacardHandler(catalogFramework);
  }

  @Test
  public void testCreateAssociatedMetacards() throws Exception {
    Metacard existing = new MetacardImpl();
    List<Metacard> children =
        ImmutableList.of(createMetacardById("child1"), createMetacardById("child2"));

    doReturn(new CreateResponseImpl(null, Collections.emptyMap(), children))
        .when(catalogFramework)
        .create(any(CreateRequest.class));

    Metacard updated = handler.create(existing, children, Associations.RELATED);

    List<Serializable> associations = updated.getAttribute(Associations.RELATED).getValues();
    assertThat(associations, is(ImmutableList.of("child1", "child2")));

    verify(catalogFramework, times(1)).create(Matchers.any(CreateRequest.class));
  }

  @Test
  public void testCreateEmptyAssociatedMetacards() throws Exception {
    Metacard existing = new MetacardImpl();
    Metacard updated = handler.create(existing, Collections.emptyList(), Associations.RELATED);

    assertThat(existing, sameInstance(updated));

    verify(catalogFramework, never()).create(Matchers.any(CreateRequest.class));
  }

  @Test
  public void testCreateNoChangesAssociatedMetacards() throws Exception {
    Metacard existing = createParentMetacard(ImmutableList.of("child1", "child2"));
    List<Metacard> children =
        ImmutableList.of(createMetacardById("child1"), createMetacardById("child2"));

    Metacard updated = handler.create(existing, children, Associations.RELATED);

    assertThat(existing, sameInstance(updated));

    verify(catalogFramework, never()).create(Matchers.any(CreateRequest.class));
  }

  @Test
  public void testCreateDeletedAssociatedMetacards() throws Exception {
    Metacard existing = createParentMetacard(ImmutableList.of("child1", "child2"));
    List<Metacard> children = ImmutableList.of(createMetacardById("child1"));

    Metacard updated = handler.create(existing, children, Associations.RELATED);

    List<Serializable> associations = updated.getAttribute(Associations.RELATED).getValues();
    assertThat(associations, is(ImmutableList.of("child1", "child2")));

    verify(catalogFramework, never()).create(Matchers.any(CreateRequest.class));
  }

  private Metacard createParentMetacard(List<Serializable> associatedIds) {
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Associations.RELATED, associatedIds));
    return metacard;
  }

  private Metacard createMetacardById(String id) {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId(id);
    return metacard;
  }
}
