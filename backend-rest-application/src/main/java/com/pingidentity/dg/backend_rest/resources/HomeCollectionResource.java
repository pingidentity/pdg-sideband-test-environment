package com.pingidentity.dg.backend_rest.resources;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;

import com.pingidentity.dg.backend_rest.api.Home;
import com.pingidentity.dg.backend_rest.db.BaseDao;

/**
 * {@link Home} collection resource.
 *
 */
@PermitAll
@Resource
@Path("/homes")
public class HomeCollectionResource extends BaseCollectionResource<Home> {

  @Override
  protected BaseDao<Home> getEntityDao() {
    return homeDao;
  }

  /**
   * Retrieve the {@link HomeResource} class.
   *
   * @return The {@link HomeResource} class.
   */
  @Path("{id}")
  public Class<HomeResource> instance() {
    return HomeResource.class;
  }

}
