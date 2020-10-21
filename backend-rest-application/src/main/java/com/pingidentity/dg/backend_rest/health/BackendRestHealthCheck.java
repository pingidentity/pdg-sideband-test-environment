package com.pingidentity.dg.backend_rest.health;

import com.codahale.metrics.health.HealthCheck;
import com.pingidentity.dg.backend_rest.api.Hub;

import lombok.RequiredArgsConstructor;

/**
 * The backend health check.
 *
 */
@RequiredArgsConstructor
public class BackendRestHealthCheck extends HealthCheck {

  private final Hub hub;

  @Override
  protected Result check() throws Exception {
    if (hub == null) {
      return Result.unhealthy("Home is null");
    }
    if (hub.getHomes().size() == 0) {
      return Result.unhealthy("No homes available");
    }
    return Result.healthy();
  }

}
