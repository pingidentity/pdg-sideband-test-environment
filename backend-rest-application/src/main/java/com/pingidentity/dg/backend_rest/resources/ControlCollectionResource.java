package com.pingidentity.dg.backend_rest.resources;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;

import com.pingidentity.dg.backend_rest.api.Control;
import com.pingidentity.dg.backend_rest.db.BaseDao;

/**
 * A {@link Control} collection resource.
 *
 */
@PermitAll
@Resource
@Path("/controls")
public class ControlCollectionResource extends BaseCollectionResource<Control> {

  private static final String PUT_NOT_SUPPORTED = "PUT_NOT_SUPPORTED";

  @Override
  public Control put(Control newEntity) {
    throw new UnsupportedOperationException(PUT_NOT_SUPPORTED);
  }

  @Override
  protected BaseDao<Control> getEntityDao() {
    return controlDao;
  }

  /**
   * Gets the {@link ControlResource} class.
   *
   * @return The {@link ControlResource} class.
   */
  @Path("{id}")
  public Class<ControlResource> instance() {
    return ControlResource.class;
  }

}
