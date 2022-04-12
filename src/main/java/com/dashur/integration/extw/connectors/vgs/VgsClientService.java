package com.dashur.integration.extw.connectors.vgs;

import com.dashur.integration.extw.connectors.everymatrix.data.*;
import java.math.BigDecimal;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/")
@RegisterRestClient
public interface VgsClientService {
  @GET
  @Path("/authenticate.do")
  @Produces(MediaType.APPLICATION_XML)
  String authenticate(@QueryParam("token") String token, @QueryParam("hash") String hash);

  @GET
  @Path("/ChangeBalance.aspx")
  @Produces(MediaType.APPLICATION_XML)
  String transaction(
      @QueryParam("userId") String userId,
      @QueryParam("Amount") BigDecimal amount,
      @QueryParam("TransactionId") String transactionId,
      @QueryParam("TrnType") String transactionType,
      @QueryParam("TrnDescription") String transactionDescription,
      @QueryParam("roundId") String roundId,
      @QueryParam("gameId") String gameId,
      @QueryParam("History") String history,
      @QueryParam("isRoundFinished") Boolean isRoundFinished,
      @QueryParam("hash") String hash);

  @GET
  @Path("/getbalance.do")
  @Produces(MediaType.APPLICATION_XML)
  String balance(@QueryParam("userId") String userId, @QueryParam("hash") String hash);
}
