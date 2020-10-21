package com.pingidentity.dg.backend_rest;

import java.io.File;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import lombok.Getter;

/**
 * The backend-rest configuration class. DropWizard parses this configuration
 * from a config.yml file.
 *
 */
@Getter
public class BackendRestConfiguration extends Configuration {

  /**
   * The application-specific configuration object.
   *
   */
  @Getter
  public static class Backend {

    @NotNull
    private File data;

    private List<String> bannedUsernames;
  }

  @NotNull
  @Valid
  private Backend backend;

}
