package com.dashur.integration.commons;

import com.dashur.integration.commons.utils.CommonUtils;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;

@Slf4j
// @Provider // Enable to allow request logging
public class LoggingFilter implements ContainerRequestFilter {
  @Context HttpRequest request;

  @Override
  public void filter(ContainerRequestContext context) throws IOException {
    String method = request.getHttpMethod();
    String address = request.getRemoteAddress();
    String path = request.getUri().getPath();
    MediaType mediaType = request.getHttpHeaders().getMediaType();
    Map<String, String> headers = Maps.newHashMap();
    Map<String, String> params = Maps.newHashMap();
    Map<String, String> forms = Maps.newHashMap();
    request
        .getHttpHeaders()
        .getRequestHeaders()
        .forEach((key, list) -> headers.put(key, list.get(0)));
    request.getUri().getQueryParameters().forEach((key, list) -> params.put(key, list.get(0)));
    if (mediaType != null && mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
      request.getFormParameters().forEach((key, list) -> forms.put(key, list.get(0)));
    }
    // don't log /health, its spammy
    if (!path.startsWith("/health")) {
      log.debug(
          "\nmethod: {}\naddress: {}\npath: {}\nmedia-type: {}\nheaders: {}\nforms: {}\nparams: {}\nstream: {}\n",
          method,
          address,
          path,
          mediaType,
          CommonUtils.jsonToString(headers),
          CommonUtils.jsonToString(forms),
          CommonUtils.jsonToString(params),
          request.getInputStream().available());
    }
  }
}
