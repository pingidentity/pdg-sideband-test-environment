package com.pingidentity.dg.smart_hub.api;

import java.util.Objects;

import javax.validation.constraints.NotEmpty;

import com.pingidentity.dg.smart_hub.api.validation.Create;
import com.pingidentity.dg.smart_hub.api.validation.Link;
import com.pingidentity.dg.smart_hub.api.validation.FullUpdate;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a top-level entity that can be created, updated, and deleted. Two
 * entities are equivalent if their ids are equivalent (ignoring other
 * attributes).
 *
 */
public abstract class Entity {

  @Getter
  @Setter
  @NotEmpty(groups = { Link.class })
  private String id;

  @Getter
  @Setter
  @NotEmpty(groups = { Create.class, FullUpdate.class })
  private String name;

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Entity) {
      return Objects.equals(getId(), ((Entity) obj).getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

}
