package com.pingidentity.dg.backend_rest.resources;

import javax.annotation.security.PermitAll;

import com.pingidentity.dg.backend_rest.api.Person;
import com.pingidentity.dg.backend_rest.db.BaseDao;

/**
 * {@link Person} resource.
 *
 */
@PermitAll
public class PersonResource extends BaseInstanceResource<Person> {

  @Override
  protected BaseDao<Person> getEntityDao() {
    return personDao;
  }
}
