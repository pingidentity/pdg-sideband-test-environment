package com.pingidentity.dg.smart_hub.resources;

import javax.annotation.security.PermitAll;

import com.pingidentity.dg.smart_hub.api.Person;
import com.pingidentity.dg.smart_hub.db.BaseDao;

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
