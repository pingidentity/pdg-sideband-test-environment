package com.pingidentity.dg.backend_rest.auth;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import io.dropwizard.auth.AuthFilter;

/**
 *
 * Auth filter for backend-rest-application. Reads credentials from an HTTP
 * header named BERA-USER and attempts to authenticate using the value as a
 * person ID.
 *
 * @param <P> The principal type.
 *
 */
public class BackendRestAuthFilter<P extends Principal>
    extends AuthFilter<BackendRestCredentials, P> {

  private static final String HEADER_NAME = "BERA-USER";

  private BackendRestAuthFilter() {
    // do nothing
  }

  @Override
  public void filter(ContainerRequestContext requestContext)
      throws IOException {
    BackendRestCredentials credentials = getCredentials(
        requestContext.getHeaders().getFirst(HEADER_NAME));
    if (!authenticate(requestContext, credentials,
        SecurityContext.BASIC_AUTH)) {
      throw new WebApplicationException(
          unauthorizedHandler.buildResponse(prefix, realm));
    }
  }

  @Nullable
  private BackendRestCredentials getCredentials(String header) {
    BackendRestCredentials credentials = new BackendRestCredentials();
    credentials.setPersonId(header);
    return credentials;
  }

  /**
   * Builder for {@link BackendRestAuthFilter}.
   *
   * @param <P> The principal type.
   */
  public static class Builder<P extends Principal> extends
      AuthFilter.AuthFilterBuilder<BackendRestCredentials, P, AuthFilter<BackendRestCredentials, P>> {

    @Override
    protected AuthFilter<BackendRestCredentials, P> newInstance() {
      return new BackendRestAuthFilter<P>();
    }

  }

}
