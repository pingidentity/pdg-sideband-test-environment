package com.pingidentity.dg.backend_rest.api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * A device control.
 *
 */
@Getter
@Setter
public class Control extends Entity {

  @Min(0)
  private Number value;

  @NotBlank
  private String status;

  private Integer remaining;

  @NotNull
  private Boolean readOnly;
}
