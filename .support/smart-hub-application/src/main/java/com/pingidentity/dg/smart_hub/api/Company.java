package com.pingidentity.dg.smart_hub.api;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * A company.
 *
 */
@Getter
@Setter
public class Company {

  @NotBlank
  private String name;
  private String catchPhrase;
  private String bs;
}
