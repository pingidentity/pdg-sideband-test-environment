package com.pingidentity.dg.smart_hub.resources;

import java.util.List;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.pingidentity.dg.smart_hub.api.Device;
import com.pingidentity.dg.smart_hub.api.Home;
import com.pingidentity.dg.smart_hub.api.Person;
import com.pingidentity.dg.smart_hub.api.validation.Link;
import com.pingidentity.dg.smart_hub.db.BaseDao;

/**
 * A {@link Home} resource.
 *
 */
@PermitAll
public class HomeResource extends BaseInstanceResource<Home> {

  @Override
  protected BaseDao<Home> getEntityDao() {
    return homeDao;
  }

  /**
   * Retrieves the devices for a home.
   *
   * @param homeId The home id.
   * @return The devices for the room.
   */
  @GET
  @Path("devices")
  public List<Device> devices(@PathParam("id") String homeId) {

    return deviceDao.findAllByHome(homeId);
  }

  /**
   * Link a device to a home.
   *
   * @param homeId The home id.
   * @param device The device to link.
   * @return The home with the device added, if both exist.
   */
  @PUT
  @Path("devices")
  public Optional<Home> putDevices(@PathParam("id") String homeId,
      @Valid @ConvertGroup(to = Link.class) Device device) {
    return deviceDao.findById(device.getId())
        .flatMap(foundDevice -> homeDao.linkDevice(homeId, foundDevice));
  }

  /**
   * Retrieve a device by home and device id.
   *
   * @param homeId   The home id.
   * @param deviceId The device id.
   * @return The device, if it exists.
   */
  @GET
  @Path("devices/{deviceId}")
  public Optional<Device> device(@PathParam("id") String homeId,
      @PathParam("deviceId") String deviceId) {
    return deviceDao.findByHomeAndId(homeId, deviceId);
  }

  /**
   * Retrieve members by home id.
   *
   * @param homeId The home id.
   * @return The list of persons.
   */
  @GET
  @Path("members")
  public List<Person> members(@PathParam("id") String homeId) {
    return personDao.findAllByHome(homeId);
  }

  /**
   * Link a person to a home, if they exist.
   *
   * @param homeId The home id.
   * @param person The person to link.
   * @return The home with the person added, if both exist.
   */
  @PUT
  @Path("members")
  public Optional<Home> putMembers(@PathParam("id") String homeId,
      @Valid @ConvertGroup(to = Link.class) Person person) {
    return personDao.findById(person.getId())
        .flatMap(foundPerson -> homeDao.linkPerson(homeId, foundPerson));
  }

  /**
   * Retrieve a person by home id and username.
   *
   * @param homeId   The home id.
   * @param personId The person id.
   * @return The person, if it exists.
   */
  @GET
  @Path("members/{personId}")
  public Optional<Person> getMember(@PathParam("id") String homeId,
      @PathParam("personId") String personId) {
    return personDao.findByHomeAndId(homeId, personId);
  }

  /**
   * Remove a person from the home.
   *
   * @param homeId   The home id.
   * @param personId The person id.
   * @return The home with the person removed, both exist.
   */
  @DELETE
  @Path("members/{personId}")
  public Optional<Home> deleteMember(@PathParam("id") String homeId,
      @PathParam("personId") String personId) {
    return personDao.findByHomeAndId(homeId, personId)
        .flatMap(person -> homeDao.unlinkPerson(homeId, person));
  }
}
