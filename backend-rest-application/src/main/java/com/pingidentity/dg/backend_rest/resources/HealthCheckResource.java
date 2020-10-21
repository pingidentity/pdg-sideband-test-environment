package com.pingidentity.dg.backend_rest.resources;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A resource without security protection to verify a healthy server.
 *
 */
@Resource
@Path("/health-check")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckResource {

  private static final String HEALTHY = "HEALTHY";

  /**
   * Health check.
   *
   * @return A hard-coded string.
   */
  @GET
  public String healthCheck() {
    return HEALTHY;
  }
}
