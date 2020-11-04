package com.pingidentity.dg.smart_hub;

import java.io.IOException;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pingidentity.dg.smart_hub.api.Hub;
import com.pingidentity.dg.smart_hub.auth.SmartHubAuthFilter;
import com.pingidentity.dg.smart_hub.auth.SmartHubAuthenticator;
import com.pingidentity.dg.smart_hub.core.BannedUsernamesResponseHeaderFilter;
import com.pingidentity.dg.smart_hub.core.ConstantResponseHeaderFilter;
import com.pingidentity.dg.smart_hub.core.EntityRepository;
import com.pingidentity.dg.smart_hub.core.HeaderReflectionFilter;
import com.pingidentity.dg.smart_hub.db.ControlDao;
import com.pingidentity.dg.smart_hub.db.DeviceDao;
import com.pingidentity.dg.smart_hub.db.HomeDao;
import com.pingidentity.dg.smart_hub.db.PersonDao;
import com.pingidentity.dg.smart_hub.health.SmartHubHealthCheck;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * The smart-hub-application entrypoint.
 *
 */
@Slf4j
public class SmartHubApplication extends Application<SmartHubConfiguration> {

  private static final String APP_ID_NAME = "X-App-Id";
  private static final String POWERED_BY_NAME = "X-Powered-By";
  private static final String POWERED_BY_VALUE = "SmartHub Server Supreme";

  /**
   * Main method.
   *
   * @param args The main method arguments.
   * @throws Exception If a problem occurs at runtime.
   */
  public static void main(final String[] args) throws Exception {
    new SmartHubApplication().run(args);
  }

  @Override
  public String getName() {
    return "smart-hub";
  }

  @Override
  public void initialize(final Bootstrap<SmartHubConfiguration> bootstrap) {
    bootstrap.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
  }

  @Override
  public void run(final SmartHubConfiguration configuration,
      final Environment environment) {

    log.debug("Parsing data...");

    try {
      Hub hub = environment.getObjectMapper()
          .readValue(configuration.getSmartHub().getData(), Hub.class);

      log.debug("Registering health checks...");
      environment.healthChecks().register("hub", new SmartHubHealthCheck(hub));

      // Configure the server to check that the SH-USER header is contained in
      // the set of hub persons.
      log.debug("Setting up security...");
      environment.jersey()
          .register(new AuthDynamicFeature(new SmartHubAuthFilter.Builder<>()
              .setAuthenticator(new SmartHubAuthenticator(hub.getPersons()))
              .buildAuthFilter()));
      environment.jersey().register(RolesAllowedDynamicFeature.class);

      log.debug("Adding response filters...");

      environment.jersey().register(new HeaderReflectionFilter(APP_ID_NAME));
      environment.jersey().register(
          new ConstantResponseHeaderFilter(POWERED_BY_NAME, POWERED_BY_VALUE));
      environment.jersey().register(new BannedUsernamesResponseHeaderFilter(
          configuration.getSmartHub().getBannedUsernames()));

      // Set up dependency injection
      log.debug("Registering binders");
      environment.jersey().getResourceConfig().register(new AbstractBinder() {
        @Override
        protected void configure() {

          /* @formatter:off */
          bind(new EntityRepository(hub)).to(EntityRepository.class);
          bind(DeviceDao.class).to(DeviceDao.class);
          bind(HomeDao.class).to(HomeDao.class);
          bind(PersonDao.class).to(PersonDao.class);
          bind(ControlDao.class).to(ControlDao.class);
          /* @formatter:on */
        }
      }).packages("com.pingidentity.dg.smart_hub.resources");
    } catch (IOException e) {
      log.error("An exception was thrown when reading data", e);
    }
  }

}
