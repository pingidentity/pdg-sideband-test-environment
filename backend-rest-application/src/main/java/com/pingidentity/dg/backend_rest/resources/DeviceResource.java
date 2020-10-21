package com.pingidentity.dg.backend_rest.resources;

import javax.annotation.security.PermitAll;

import com.pingidentity.dg.backend_rest.api.Device;
import com.pingidentity.dg.backend_rest.db.BaseDao;

/**
 * A {@link Device} resource.
 *
 */
@PermitAll
public class DeviceResource extends BaseInstanceResource<Device> {

  @Override
  protected BaseDao<Device> getEntityDao() {
    return deviceDao;
  }

}
