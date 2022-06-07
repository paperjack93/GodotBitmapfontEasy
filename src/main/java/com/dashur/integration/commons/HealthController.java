package com.dashur.integration.commons;

import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
public class HealthController {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String health() {
    return String.format("OK - [%s]", new Date());
  }
}
