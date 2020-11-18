package com.pingidentity.dg.smart_hub.api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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

  private Integer callsRemaining;

  private Boolean readOnly;
}
