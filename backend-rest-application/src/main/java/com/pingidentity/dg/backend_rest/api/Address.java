package com.pingidentity.dg.backend_rest.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * An address.
 */
@Getter
@Setter
public class Address {

  @NotBlank
  private String street;
  private String suite;

  private String city;

  @NotBlank
  private String zipcode;

  @Valid
  private Coordinates geo;

}
