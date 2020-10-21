package com.pingidentity.dg.backend_rest.api;

import java.util.List;

import javax.validation.constraints.Null;

import com.pingidentity.dg.backend_rest.api.validation.Create;

import lombok.Getter;
import lombok.Setter;

/**
 * A home.
 *
 */
@Getter
@Setter
public class Home extends Entity {

  private String image;

  private Coordinates geo;

  @Null(groups = Create.class)
  private List<Person> members;

  @Null(groups = Create.class)
  private List<Device> devices;

}
