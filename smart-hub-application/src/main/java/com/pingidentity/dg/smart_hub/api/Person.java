package com.pingidentity.dg.smart_hub.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.pingidentity.dg.smart_hub.api.validation.Create;
import com.pingidentity.dg.smart_hub.api.validation.FullUpdate;

import lombok.Getter;
import lombok.Setter;

/**
 * A person.
 *
 */
@Getter
@Setter
public class Person extends Entity {

  @NotBlank(groups = { Create.class, FullUpdate.class })
  @Size(min = 5)
  private String username;

  private String email;

  private String phone;
  private String website;

  @Valid
  private Address address;

  @Valid
  private Company company;

}
