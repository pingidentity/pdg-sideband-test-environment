package com.pingidentity.dg.smart_hub.auth;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import io.dropwizard.auth.AuthFilter;

/**
 *
 * Auth filter for smart-hub-application. Reads credentials from an HTTP header
 * named SH-USER and attempts to authenticate using the value as a person ID.
 *
 * @param <P> The principal type.
 *
 */
public class SmartHubAuthFilter<P extends Principal>
    extends AuthFilter<SmartHubCredentials, P> {

  private static final String HEADER_NAME = "SH-USER";

  private SmartHubAuthFilter() {
    // do nothing
  }

  @Override
  public void filter(ContainerRequestContext requestContext)
      throws IOException {
    SmartHubCredentials credentials = getCredentials(
        requestContext.getHeaders().getFirst(HEADER_NAME));
    if (!authenticate(requestContext, credentials,
        SecurityContext.BASIC_AUTH)) {
      throw new WebApplicationException(
          unauthorizedHandler.buildResponse(prefix, realm));
    }
  }

  @Nullable
  private SmartHubCredentials getCredentials(String header) {
    SmartHubCredentials credentials = new SmartHubCredentials();
    credentials.setPersonId(header);
    return credentials;
  }

  /**
   * Builder for {@link SmartHubAuthFilter}.
   *
   * @param <P> The principal type.
   */
  public static class Builder<P extends Principal> extends
      AuthFilter.AuthFilterBuilder<SmartHubCredentials, P, AuthFilter<SmartHubCredentials, P>> {

    @Override
    protected AuthFilter<SmartHubCredentials, P> newInstance() {
      return new SmartHubAuthFilter<P>();
    }

  }

}
