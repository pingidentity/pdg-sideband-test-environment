package com.pingidentity.dg.backend_rest.resources;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;

import com.pingidentity.dg.backend_rest.api.Device;
import com.pingidentity.dg.backend_rest.db.BaseDao;

/**
 *
 * A {@link Device} collection resource.
 *
 */
@PermitAll
@Resource
@Path("/devices")
public class DeviceCollectionResource extends BaseCollectionResource<Device> {

  @Override
  protected BaseDao<Device> getEntityDao() {
    return deviceDao;
  }

  /**
   * Gets the {@link DeviceResource} class.
   *
   * @return The {@link DeviceResource} class.
   */
  @Path("{id}")
  public Class<DeviceResource> instance() {
    return DeviceResource.class;
  }

}
