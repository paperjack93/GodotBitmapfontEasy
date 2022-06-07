package com.dashur.integration.commons;

import io.vertx.core.http.HttpServerRequest;
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import org.jboss.logmanager.MDC;

@Provider
public class SpanTraceFilter implements ContainerRequestFilter, ContainerResponseFilter {

  @Context HttpServerRequest request;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String traceId = request.getHeader("x-b3-traceid");
    if (traceId != null) {
      MDC.put("x-b3-traceid", traceId);
    }
    String spanId = request.getHeader("x-b3-spanid");
    if (spanId != null) {
      MDC.put("x-b3-spanid", spanId);
    }
  }

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {
    MDC.remove("x-b3-traceid");
    MDC.remove("x-b3-spanid");
  }
}
