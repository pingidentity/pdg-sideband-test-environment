package com.pingidentity.dg.backend_rest.core;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import lombok.RequiredArgsConstructor;

/**
 * Adds a header with a comma separated list of banned usernames.
 *
 */
@RequiredArgsConstructor
public class BannedUsernamesResponseHeaderFilter
    implements ContainerResponseFilter {

  private static final String RESPONSE_HEADER_NAME = "X-Banned-Users";

  private final List<String> bannedUsernames;

  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) throws IOException {
    responseContext.getHeaders().add(RESPONSE_HEADER_NAME,
        bannedUsernames.stream().collect(Collectors.joining(",")));

  }

}
