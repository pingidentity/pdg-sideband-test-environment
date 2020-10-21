package com.pingidentity.dg.backend_rest.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pingidentity.dg.backend_rest.api.Control;
import com.pingidentity.dg.backend_rest.api.Device;
import com.pingidentity.dg.backend_rest.api.Entity;
import com.pingidentity.dg.backend_rest.api.Home;
import com.pingidentity.dg.backend_rest.api.Hub;
import com.pingidentity.dg.backend_rest.api.Person;

import lombok.Getter;
import lombok.Setter;

/**
 * Internal-only helper class tracking references to entity instances.
 */
@Getter
public class EntityRepository {

  private final Registry<Home> homes;
  private final Registry<Person> persons;
  private final Registry<Device> devices;
  private final Registry<Control> controls;

  /**
   * Constructor.
   *
   * @param hub The source hub data.
   */
  public EntityRepository(Hub hub) {

    // Generate registry entries for each of the entity types.
    homes = new Registry<>(Stream.of(new LinkedList<>(hub.getHomes()))
        .collect(Collectors.toSet()));
    persons = new Registry<>(homes.getAll().stream().map(Home::getMembers)
        .collect(Collectors.toSet()));
    devices = new Registry<>(homes.getAll().stream().map(Home::getDevices)
        .collect(Collectors.toSet()));
    controls = new Registry<>(devices.getAll().stream()
        .map(Device::getControls).collect(Collectors.toSet()));
  }

  /**
   * An entity registry for entities of a type.
   *
   * @param <E> The entity type.
   */
  @Getter
  @Setter
  public static class Registry<E extends Entity> {

    /**
     * A set of all entities for this entity type.
     */
    private Set<E> all;

    /**
     * A set-of-lists for all one-to-many relationships to this entity type.
     */
    private Set<List<E>> relationshipLists;

    /**
     * Constructor.
     *
     * @param relationshipLists A list-of-lists containing the one-to-many
     *                          relationships to this entity type.
     */
    public Registry(Set<List<E>> relationshipLists) {
      this.relationshipLists = new HashSet<>(relationshipLists);
      all = relationshipLists.stream().flatMap(Collection::stream)
          .collect(Collectors.toSet());
    }

    /**
     * Adds a new relationship list.
     *
     * @param relationshipList The relationship list.
     */
    public void addRelationshipList(List<E> relationshipList) {
      relationshipList.stream().forEach(all::add);
      relationshipLists.add(new LinkedList<>(relationshipList));
    }
  }

}