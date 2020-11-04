package com.pingidentity.dg.smart_hub.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pingidentity.dg.smart_hub.api.Entity;
import com.pingidentity.dg.smart_hub.db.BaseDao;
import com.pingidentity.dg.smart_hub.db.ControlDao;
import com.pingidentity.dg.smart_hub.db.DeviceDao;
import com.pingidentity.dg.smart_hub.db.HomeDao;
import com.pingidentity.dg.smart_hub.db.PersonDao;

/**
 * The base resource.
 *
 * @param <E> The {@link Entity} for this resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class BaseResource<E extends Entity> {

  /**
   * The home data access object.
   */
  @Inject
  protected HomeDao homeDao;

  /**
   * The device data access object.
   */
  @Inject
  protected DeviceDao deviceDao;

  /**
   * The person data access object.
   */
  @Inject
  protected PersonDao personDao;

  /**
   * The control data access object.
   */
  @Inject
  protected ControlDao controlDao;

  /**
   * Return the {@link BaseDao} for the entity associated with this resource.
   *
   * @return The {@link BaseDao} for the entity associated with this resource.
   */
  protected abstract BaseDao<E> getEntityDao();
}
