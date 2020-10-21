package com.pingidentity.dg.backend_rest;

import java.io.IOException;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pingidentity.dg.backend_rest.api.Hub;
import com.pingidentity.dg.backend_rest.auth.BackendRestAuthFilter;
import com.pingidentity.dg.backend_rest.auth.BackendRestAuthenticator;
import com.pingidentity.dg.backend_rest.core.BannedUsernamesResponseHeaderFilter;
import com.pingidentity.dg.backend_rest.core.ConstantResponseHeaderFilter;
import com.pingidentity.dg.backend_rest.core.EntityRepository;
import com.pingidentity.dg.backend_rest.db.ControlDao;
import com.pingidentity.dg.backend_rest.db.DeviceDao;
import com.pingidentity.dg.backend_rest.db.HomeDao;
import com.pingidentity.dg.backend_rest.db.PersonDao;
import com.pingidentity.dg.backend_rest.health.BackendRestHealthCheck;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * The backend rest application entrypoint.
 *
 */
@Slf4j
public class BackendRestApplication
    extends Application<BackendRestConfiguration> {

  private static final String RESPONSE_HEADER_NAME = "X-Powered-By";
  private static final String RESPONSE_HEADER_VALUE = "Backend Server Supreme";

  /**
   * Main method.
   *
   * @param args The main method arguments.
   * @throws Exception If a problem occurs at runtime.
   */
  public static void main(final String[] args) throws Exception {
    new BackendRestApplication().run(args);
  }

  @Override
  public String getName() {
    return "backend-rest";
  }

  @Override
  public void initialize(final Bootstrap<BackendRestConfiguration> bootstrap) {
    bootstrap.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
  }

  @Override
  public void run(final BackendRestConfiguration configuration,
      final Environment environment) {

    log.debug("Parsing data...");

    try {
      Hub hub = environment.getObjectMapper()
          .readValue(configuration.getBackend().getData(), Hub.class);

      log.debug("Registering health checks...");
      environment.healthChecks().register("hub",
          new BackendRestHealthCheck(hub));

      // Configure the server to check that the BERA-USER header is contained in
      // the set of hub persons.
      log.debug("Setting up security...");
      environment.jersey().register(
          new AuthDynamicFeature(new BackendRestAuthFilter.Builder<>()
              .setAuthenticator(new BackendRestAuthenticator(hub.getPersons()))
              .buildAuthFilter()));
      environment.jersey().register(RolesAllowedDynamicFeature.class);

      log.debug("Adding response filters...");

      environment.jersey().register(new ConstantResponseHeaderFilter(
          RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE));
      environment.jersey().register(new BannedUsernamesResponseHeaderFilter(
          configuration.getBackend().getBannedUsernames()));

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
      }).packages("com.pingidentity.dg.backend_rest.resources");
    } catch (IOException e) {
      log.error("An exception was thrown when reading data", e);
    }
  }

}
