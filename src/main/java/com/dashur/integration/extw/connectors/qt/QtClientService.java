package com.dashur.integration.extw.connectors.qt;

import com.dashur.integration.extw.connectors.qt.data.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface QtClientService {
  @POST
  @Path("/providers/{providerId}/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  AuthResponse authenticate(
      @HeaderParam("Pass-Key") final String passKey,
      @HeaderParam("Session-Token") final String sessionToken,
      @PathParam("providerId") final String providerId,
      final AuthRequest request);

  @GET
  @Path("/providers/{providerId}/players/{playerId}/balance")
  @Produces(MediaType.APPLICATION_JSON)
  BalanceResponse balance(
      @HeaderParam("Pass-Key") final String passKey,
      @HeaderParam("Session-Token") final String sessionToken,
      @PathParam("providerId") final String providerId,
      @PathParam("playerId") final String playerId);

  @POST
  @Path("/providers/{providerId}/withdrawal")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  WithdrawalResponse withdrawal(
      @HeaderParam("Pass-Key") final String passKey,
      @HeaderParam("Session-Token") final String sessionToken,
      @PathParam("providerId") final String providerId,
      final WithdrawalRequest request);

  @POST
  @Path("/providers/{providerId}/deposit")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  DepositResponse deposit(
      @HeaderParam("Pass-Key") final String passKey,
      @HeaderParam("Session-Token") final String sessionToken,
      @PathParam("providerId") final String providerId,
      final DepositRequest request);

  @POST
  @Path("/providers/{providerId}/rollback")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  RollbackResponse rollback(
      @HeaderParam("Pass-Key") final String passKey,
      @HeaderParam("Session-Token") final String sessionToken,
      @PathParam("providerId") final String providerId,
      final RollbackRequest request);
}
