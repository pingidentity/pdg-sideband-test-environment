package com.pingidentity.dg.backend_rest.db;

import com.pingidentity.dg.backend_rest.api.Control;
import com.pingidentity.dg.backend_rest.core.EntityRepository.Registry;

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
