package com.pingidentity.dg.backend_rest.resources;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

import com.pingidentity.dg.backend_rest.api.Entity;
import com.pingidentity.dg.backend_rest.api.validation.Update;

/**
 * The base instance resource.
 *
 * @param <E> The entity class.
 */
public abstract class BaseInstanceResource<E extends Entity>
    extends BaseResource<E> {

  /**
   * Get an entity by id.
   *
   * @param id The entity id.
   * @return The entity, if it exists.
   */
  @GET
  public Optional<E> get(@PathParam("id") @NotBlank String id) {
    return getEntityDao().findById(id);
  }

  /**
   * Update an entity.
   *
   * @param id            The entity id.
   * @param changedEntity The changed entity.
   * @return The changed entity, if it exists.
   */
  @POST
  public Optional<E> post(@PathParam("id") @NotBlank String id,
      @NotNull @Valid @ConvertGroup(to = Update.class) E changedEntity) {
    return getEntityDao().update(id, changedEntity);
  }

  /**
   * Deletes an entity.
   *
   * @param id The entity id.
   * @return The deleted entity, if it existed.
   */
  @DELETE
  public Optional<E> delete(@PathParam("id") @NotBlank String id) {
    return getEntityDao().deleteById(id);
  }

}
