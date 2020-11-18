package com.pingidentity.dg.smart_hub.api;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * Geographic coordinates.
 *
 */
@Getter
@Setter
public class Coordinates {

  @NotBlank
  private String lat;

  @NotBlank
  private String lng;
}
