package com.pingidentity.dg.smart_hub.api;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * A top-level smart home hub.
 *
 */
@Getter
@Setter
public class Hub {

  private List<Home> homes;
  private Map<String, Person> persons;

}
