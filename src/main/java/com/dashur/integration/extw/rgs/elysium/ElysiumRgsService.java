package com.dashur.integration.extw.rgs.elysium;

import com.dashur.integration.extw.rgs.RgsServiceProvider;
import com.dashur.integration.extw.rgs.data.GameHash;
import com.dashur.integration.extw.rgs.data.PlaycheckExtRequest;
import com.dashur.integration.extw.rgs.data.PlaycheckExtResponse;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v2/rgs")
@RegisterRestClient
public interface ElysiumRgsService extends RgsServiceProvider {
  @GET
  @Path("/gamehashes")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  List<GameHash> gameHashes();

  @POST
  @Path("/playcheckext")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PlaycheckExtResponse playcheckExt(final PlaycheckExtRequest request);
}
