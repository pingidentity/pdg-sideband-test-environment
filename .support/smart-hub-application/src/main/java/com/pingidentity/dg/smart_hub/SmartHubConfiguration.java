package com.pingidentity.dg.smart_hub;

import java.io.File;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import lombok.Getter;

/**
 * The smart-hub-application configuration class. DropWizard parses this
 * configuration from a config.yml file.
 *
 */
@Getter
public class SmartHubConfiguration extends Configuration {

  /**
   * The application-specific configuration object.
   *
   */
  @Getter
  public static class SmartHub {

    @NotNull
    private File data;

    private List<String> bannedUsernames;
  }

  @NotNull
  @Valid
  private SmartHub smartHub;

}
