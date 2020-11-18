package com.pingidentity.dg.smart_hub.core;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.apache.commons.lang3.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * A filter that reflects a request header value on the response.
 *
 */
@RequiredArgsConstructor
public class HeaderReflectionFilter implements ContainerResponseFilter {

  private final String headerName;

  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) throws IOException {
    String headerValue = requestContext.getHeaderString(headerName);
    if (StringUtils.isNotBlank(headerValue)) {
      responseContext.getHeaders().add(headerName, headerValue);
    }
  }

}
