package com.pingidentity.dg.backend_rest.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.pingidentity.dg.backend_rest.api.validation.Create;
import com.pingidentity.dg.backend_rest.api.validation.Update;

import lombok.Getter;
import lombok.Setter;

/**
 * A person.
 *
 */
@Getter
@Setter
public class Person extends Entity {

  @NotBlank(groups = { Create.class, Update.class })
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
