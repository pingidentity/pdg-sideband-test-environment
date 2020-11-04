package com.pingidentity.dg.smart_hub.core;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import lombok.RequiredArgsConstructor;

/**
 * Adds a constant response header.
 *
 */
@RequiredArgsConstructor
public class ConstantResponseHeaderFilter implements ContainerResponseFilter {

  private final String headerName;
  private final String headerValue;

  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) throws IOException {
    responseContext.getHeaders().add(headerName, headerValue);
  }

}
