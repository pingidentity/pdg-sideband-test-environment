package com.pingidentity.dg.backend_rest.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pingidentity.dg.backend_rest.api.Device;
import com.pingidentity.dg.backend_rest.api.Home;
import com.pingidentity.dg.backend_rest.api.Person;
import com.pingidentity.dg.backend_rest.core.EntityRepository.Registry;

/**
 *
 * Data access object for {@link Home}.
 *
 */
public class HomeDao extends BaseDao<Home> {

  @Override
  public Home create(Home entity) {
    List<Person> members = new ArrayList<>();
    List<Device> devices = new ArrayList<>();
    entityRepository.getPersons().addRelationshipList(members);
    entityRepository.getDevices().addRelationshipList(devices);
    entity.setMembers(members);
    entity.setDevices(devices);
    return super.create(entity);
  }

  /**
   * Link a device.
   *
   * @param homeId The home id.
   * @param device The device.
   * @return The home, with the device added.
   */
  public Optional<Home> linkDevice(String homeId, Device device) {
    Optional<Home> foundHome = findById(homeId);
    foundHome.ifPresent(home -> home.getDevices().add(device));
    return foundHome;
  }

  /**
   * Unlink a device.
   *
   * @param homeId The home id.
   * @param device The device.
   * @return The home, with the device removed.
   */
  public Optional<Home> unlinkDevice(String homeId, Device device) {
    Optional<Home> foundHome = findById(homeId);
    foundHome.ifPresent(home -> home.getDevices().remove(device));
    return foundHome;
  }

  /**
   * Link a person.
   *
   * @param homeId The home id.
   * @param person The person.
   * @return The home, if it exists.
   */
  public Optional<Home> linkPerson(String homeId, Person person) {
    Optional<Home> foundHome = findById(homeId);
    foundHome.ifPresent(home -> home.getMembers().add(person));
    return foundHome;
  }

  /**
   * Unlink a person.
   *
   * @param homeId The home id.
   * @param person The person.
   * @return The home, if it exists.
   */
  public Optional<Home> unlinkPerson(String homeId, Person person) {
    Optional<Home> foundHome = findById(homeId);
    foundHome.ifPresent(home -> home.getMembers().remove(person));
    return foundHome;
  }

  @Override
  public Optional<Home> update(String id, Home entity) {
    return Optional.of(entity);
  }

  @Override
  protected Registry<Home> getRegistry() {
    return entityRepository.getHomes();
  }

}
