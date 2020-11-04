package com.pingidentity.dg.smart_hub.db;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pingidentity.dg.smart_hub.api.Device;
import com.pingidentity.dg.smart_hub.api.Home;
import com.pingidentity.dg.smart_hub.core.EntityRepository.Registry;

/**
 * Data access object for {@link Device}.
 *
 */
public class DeviceDao extends BaseDao<Device> {

  @Override
  public Device create(Device entity) {
    Device device = super.create(entity);

    // Add all controls as well
    entityRepository.getControls().addRelationshipList(device.getControls());
    return device;
  }

  /**
   * Finds a device by home and id.
   *
   * @param homeId   The home id.
   * @param deviceId The device id.
   * @return The device, if it exists.
   */
  public Optional<Device> findByHomeAndId(String homeId, String deviceId) {
    return findAllByHome(homeId).stream()
        .filter(device -> deviceId.equals(device.getId())).findFirst();
  }

  /**
   * Find all devices by home.
   *
   * @param homeId The home id.
   * @return The home's devices, or an empty list if the home does not exist.
   */
  public List<Device> findAllByHome(String homeId) {

    return entityRepository.getHomes().getAll().stream()
        .filter(home -> homeId.equals(home.getId())).map(Home::getDevices)
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  protected Registry<Device> getRegistry() {
    return entityRepository.getDevices();
  }
}
