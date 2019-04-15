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

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Core;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.operation.impl.CreateRequestImpl;
import ddf.catalog.operation.impl.DeleteRequestImpl;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.codice.ddf.catalog.ui.metacard.query.data.metacard.QueryMetacardImpl;
import org.opengis.filter.Filter;

public class AssociatedMetacardHandler {

  private final CatalogFramework catalogFramework;

  private final FilterBuilder filterBuilder;

  public AssociatedMetacardHandler(CatalogFramework catalogFramework, FilterBuilder filterBuilder) {
    this.catalogFramework = catalogFramework;
    this.filterBuilder = filterBuilder;
  }

  public Metacard handleAllOperations(
      Metacard parent, List<Metacard> associatedMetacards, String association)
      throws IngestException, SourceUnavailableException {
    return create(parent, associatedMetacards, association);
  }

  public Metacard create(Metacard parent, List<Metacard> associatedMetacards, String association)
      throws IngestException, SourceUnavailableException {
    List<String> existingAssociations = getAssociations(parent, association);

    List<Metacard> newMetacards =
        associatedMetacards
            .stream()
            .filter(Objects::nonNull)
            .filter(child -> !existingAssociations.contains(child.getId()))
            .map(QueryMetacardImpl::new)
            .collect(Collectors.toList());

    if (!newMetacards.isEmpty()) {
      MetacardType metacardType = parent.getMetacardType();
      Metacard metacard = new MetacardImpl(parent, metacardType);

      catalogFramework.create(new CreateRequestImpl(newMetacards));

      List<String> associations =
          associatedMetacards.stream().map(Metacard::getId).collect(Collectors.toList());
      metacard.setAttribute(new AttributeImpl(association, (Serializable) associations));

      return metacard;
    }

    return parent;
  }

  public Metacard delete(Metacard parent, List<Metacard> associatedMetacards, String association)
      throws IngestException, SourceUnavailableException {
    List<String> existingAssociations = getAssociations(parent, association);

    String[] removed =
        associatedMetacards
            .stream()
            .filter(metacard -> existingAssociations.contains(metacard.getId()))
            .map(Metacard::getId)
            .toArray(String[]::new);

    if (removed.length != 0) {
      MetacardType metacardType = parent.getMetacardType();
      Metacard metacard = new MetacardImpl(parent, metacardType);

      catalogFramework.delete(new DeleteRequestImpl(removed));
      existingAssociations.removeAll(Arrays.asList(removed));

      metacard.setAttribute(new AttributeImpl(association, (Serializable) existingAssociations));

      return metacard;
    }

    return parent;
  }

  public Metacard update() {
    /*
    List<String> metacardIds = associatedMetacards.stream().map(Metacard::getId).collect(Collectors.toList());

    Filter filter = getMetacardIdCollectionFilter(metacardIds);
    List<Metacard> existingAssociatedMetacards = catalogFramework.query(new QueryRequestImpl(new QueryImpl(filter)))
        .getResults()
        .stream()
        .map(Result::getMetacard)
        .collect(Collectors.toList());

    // for
    */

    return new MetacardImpl();
  }

  private Filter getMetacardIdFilter(String id) {
    return filterBuilder.attribute(Core.ID).like().text(id);
  }

  private Filter getMetacardIdCollectionFilter(List<String> metacardIds) {
    return filterBuilder.anyOf(
        metacardIds.stream().map(this::getMetacardIdFilter).collect(Collectors.toList()));
  }

  private boolean hasChanges(Metacard existing, Metacard updated) {
    Optional<AttributeDescriptor> difference =
        existing
            .getMetacardType()
            .getAttributeDescriptors()
            .stream()
            .filter(
                attributeDesc ->
                    !Objects.equals(
                        existing.getAttribute(attributeDesc.getName()),
                        updated.getAttribute(attributeDesc.getName())))
            .findFirst();
    return difference.isPresent();
  }

  private List<String> getAssociations(Metacard metacard, String association) {
    Attribute attr = metacard.getAttribute(association);

    if (attr == null) {
      return Collections.emptyList();
    }

    return attr.getValues()
        .stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .collect(Collectors.toList());
  }
}
