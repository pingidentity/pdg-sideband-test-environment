package com.pingidentity.dg.smart_hub.health;

import com.codahale.metrics.health.HealthCheck;
import com.pingidentity.dg.smart_hub.api.Hub;

import lombok.RequiredArgsConstructor;

/**
 * The smart-hub-application health check.
 *
 */
@RequiredArgsConstructor
public class SmartHubHealthCheck extends HealthCheck {

  private final Hub hub;

  @Override
  protected Result check() throws Exception {
    if (hub == null) {
      return Result.unhealthy("Hub is null");
    }
    if (hub.getHomes().size() == 0) {
      return Result.unhealthy("No homes available");
    }
    return Result.healthy();
  }

}
