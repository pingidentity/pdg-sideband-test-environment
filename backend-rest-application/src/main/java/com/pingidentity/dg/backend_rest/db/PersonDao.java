package com.pingidentity.dg.backend_rest.db;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pingidentity.dg.backend_rest.api.Home;
import com.pingidentity.dg.backend_rest.api.Person;
import com.pingidentity.dg.backend_rest.core.EntityRepository.Registry;

/**
 * Data access object for {@link Person}.
 *
 */
public class PersonDao extends BaseDao<Person> {

  /**
   * Find persons by home.
   *
   * @param homeId The home id.
   * @return The list of persons for the home.
   */
  public List<Person> findAllByHome(String homeId) {

    /* @formatter:off */
    return entityRepository.getHomes().getAll().stream()
        .filter(home -> homeId.equals(home.getId()))
        .map(Home::getMembers)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    /* @formatter:on */
  }

  /**
   * Find a person by home and id.
   *
   * @param homeId   The home id.
   * @param personId The person id.
   * @return The person, if it exists.
   */
  public Optional<Person> findByHomeAndId(String homeId, String personId) {
    return findAllByHome(homeId).stream()
        .filter(person -> personId.equals(person.getId())).findFirst();
  }

  @Override
  protected Registry<Person> getRegistry() {
    return entityRepository.getPersons();
  }
}
