package com.pingidentity.dg.smart_hub.resources;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;

import com.pingidentity.dg.smart_hub.api.Person;
import com.pingidentity.dg.smart_hub.db.BaseDao;

/**
 *
 * {@link Person} collection resource.
 *
 */
@PermitAll
@Resource
@Path("/persons")
public class PersonCollectionResource extends BaseCollectionResource<Person> {

  @Override
  protected BaseDao<Person> getEntityDao() {
    return personDao;
  }

  /**
   * Get the {@link PersonResource} class.
   *
   * @return The {@link PersonResource} class.
   */
  @Path("{id}")
  public Class<PersonResource> instance() {
    return PersonResource.class;
  }

}
