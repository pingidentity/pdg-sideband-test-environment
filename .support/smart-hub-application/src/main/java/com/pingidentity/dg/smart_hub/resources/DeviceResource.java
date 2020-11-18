package com.pingidentity.dg.smart_hub.resources;

import javax.annotation.security.PermitAll;

import com.pingidentity.dg.smart_hub.api.Device;
import com.pingidentity.dg.smart_hub.db.BaseDao;

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
