package com.pingidentity.dg.smart_hub.db;

import com.pingidentity.dg.smart_hub.api.Control;
import com.pingidentity.dg.smart_hub.core.EntityRepository.Registry;

/**
 * Data access object for {@link Control}.
 *
 */
public class ControlDao extends BaseDao<Control> {

  @Override
  protected Registry<Control> getRegistry() {
    return entityRepository.getControls();
  }

}
