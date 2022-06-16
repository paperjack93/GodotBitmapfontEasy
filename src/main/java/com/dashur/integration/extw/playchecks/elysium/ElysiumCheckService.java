package com.dashur.integration.extw.playchecks.elysium;

import com.dashur.integration.extw.playchecks.CheckServiceProvider;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/game-history")
@RegisterRestClient
public interface ElysiumCheckService extends CheckServiceProvider {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  void url();

}
