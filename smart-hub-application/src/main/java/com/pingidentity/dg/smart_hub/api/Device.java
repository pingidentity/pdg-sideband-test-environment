package com.pingidentity.dg.smart_hub.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.pingidentity.dg.smart_hub.api.validation.Create;
import com.pingidentity.dg.smart_hub.api.validation.FullUpdate;

import lombok.Getter;
import lombok.Setter;

/**
 * A device.
 *
 */
@Getter
@Setter
public class Device extends Entity {

  private String type;
  private String product;
  private String vendor;

  @Null(groups = FullUpdate.class)
  @NotNull(groups = Create.class)
  @NotEmpty(groups = Create.class)
  @Valid
  private List<Control> controls;

}
