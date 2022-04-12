package com.dashur.integration.extw.connectors.everymatrix;

import com.dashur.integration.extw.connectors.everymatrix.data.AuthenticateRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.AuthenticateResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.BalanceRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.BalanceResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.BetRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.BetResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.CancelRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.CancelResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.WinRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.WinResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
public interface EveryMatrixClientService {
  @POST
  @Path("/{provider}/Authenticate")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  AuthenticateResponse authenticate(
      @PathParam("provider") String provider, final AuthenticateRequest request);

  @POST
  @Path("/{provider}/GetBalance")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  BalanceResponse balance(@PathParam("provider") String provider, final BalanceRequest request);

  @POST
  @Path("/{provider}/Bet")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  BetResponse bet(@PathParam("provider") String provider, final BetRequest request);

  @POST
  @Path("/{provider}/Win")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  WinResponse win(@PathParam("provider") String provider, final WinRequest request);

  @POST
  @Path("/{provider}/Cancel")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  CancelResponse cancel(@PathParam("provider") String provider, final CancelRequest request);
}
