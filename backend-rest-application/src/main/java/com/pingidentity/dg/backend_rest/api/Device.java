package com.pingidentity.dg.backend_rest.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.pingidentity.dg.backend_rest.api.validation.Create;
import com.pingidentity.dg.backend_rest.api.validation.Update;

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

  @Null(groups = Update.class)
  @NotNull(groups = Create.class)
  @NotEmpty(groups = Create.class)
  @Valid
  private List<Control> controls;

}
