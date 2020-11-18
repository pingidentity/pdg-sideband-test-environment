package com.pingidentity.dg.smart_hub.db;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import com.pingidentity.dg.smart_hub.api.Entity;
import com.pingidentity.dg.smart_hub.core.EntityRepository;
import com.pingidentity.dg.smart_hub.core.EntityRepository.Registry;

/**
 * Base data access object.
 *
 * @param <E> The entity type.
 */
public abstract class BaseDao<E extends Entity> {

  /**
   * The entity registry.
   */
  @Inject
  protected EntityRepository entityRepository;

  /**
   * Get the registry for the entity type represented by this data access
   * object.
   *
   * @return The entity's registry.
   */
  protected abstract Registry<E> getRegistry();

  /**
   * Find an entity by id.
   *
   * @param id The entity id.
   * @return The entity, if it exists.
   */
  public final Optional<E> findById(String id) {
    return getRegistry().getAll().stream()
        .filter(entity -> id.equals(entity.getId())).findFirst();
  }

  /**
   * Find all entities.
   *
   * @return All entities.
   */
  public final List<E> findAll() {
    return new LinkedList<>(getRegistry().getAll());
  }

  /**
   * Delete an entity by id.
   *
   * @param id The entity id.
   * @return The deleted entity, if it existed.
   */
  public Optional<E> deleteById(String id) {
    Optional<E> found = findById(id);

    // delete the entity from the registry
    found.ifPresent(entity -> {
      getRegistry().getAll().remove(entity);
      getRegistry().getRelationshipLists()
          .forEach(refList -> refList.remove(entity));
    });

    return found;
  }

  /**
   * Create a new entity.
   *
   * @param entity The new entity.
   * @return The created entity.
   */
  public E create(E entity) {
    entity.setId(generateUuid());

    // Add the entity to the registry without any links.
    getRegistry().getAll().add(entity);
    return entity;
  }

  /**
   * Updates an entity.
   *
   * @param id     The id of the entity to update.
   * @param entity The changed entity.
   * @return The changed entity, if it exists.
   */
  public Optional<E> update(String id, E entity) {
    Optional<E> found = findById(id);
    if (found.isPresent()) {
      entity.setId(id);
      getRegistry().getAll().add(entity);
      getRegistry().getRelationshipLists().stream()
          .forEach(relationshipList -> relationshipList.add(entity));
      return Optional.of(entity);
    }
    return found;
  }

  /**
   * Generate a UUID.
   *
   * @return The UUID.
   */
  protected String generateUuid() {
    return UUID.randomUUID().toString();
  }

}
