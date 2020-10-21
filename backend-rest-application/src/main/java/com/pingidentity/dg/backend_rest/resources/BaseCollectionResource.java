package com.pingidentity.dg.backend_rest.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;

import com.pingidentity.dg.backend_rest.api.Entity;
import com.pingidentity.dg.backend_rest.api.validation.Create;

/**
 * Base collection resource for an entity.
 *
 * @param <E> The entity class.
 */
public abstract class BaseCollectionResource<E extends Entity>
    extends BaseResource<E> {

  private static final String STARTS_WITH = "sw";
  private static final String ENDS_WITH = "ew";
  private static final String CONTAINS = "co";

  /**
   * Get a list of all entities, optionally filtered by name.
   *
   * @param nameFilter  The name filter.
   * @param compareType The name filter comparison type. Valid values are "sw"
   *                    (starts with), "ew" (ends with), "co" (contains).
   * @return The list of entities that match the name filter, or all entities if
   *         no filter is provided.
   */
  @GET
  public List<E> get(@QueryParam("name") @Size(min = 3) String nameFilter,
      @QueryParam("compare") String compareType) {

    return getEntityDao().findAll().stream()
        .filter(entity -> matchName(entity, nameFilter, compareType))
        .collect(Collectors.toList());

  }

  private boolean matchName(E entity, String nameFilter, String compareType) {

    String name = entity.getName();

    // If there is no name filter, everything matches
    if (StringUtils.isBlank(nameFilter)) {
      return true;
    }

    // Use the compare type to determine the comparison used
    if (STARTS_WITH.equalsIgnoreCase(compareType)) {
      return StringUtils.startsWithIgnoreCase(name, nameFilter);
    } else if (ENDS_WITH.equalsIgnoreCase(compareType)) {
      return StringUtils.endsWithIgnoreCase(name, nameFilter);
    } else if (CONTAINS.equalsIgnoreCase(compareType)) {
      return StringUtils.containsIgnoreCase(name, nameFilter);
    }
    return StringUtils.equalsIgnoreCase(name, nameFilter);
  }

  /**
   * Creates a new entity.
   *
   * @param newEntity The entity to create.
   * @return The created entity.
   */
  @PUT
  public E put(@NotNull @Valid @ConvertGroup(to = Create.class) E newEntity) {
    return getEntityDao().create(newEntity);
  }

}
