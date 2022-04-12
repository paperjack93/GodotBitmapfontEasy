package com.dashur.integration.extw.connectors.parimatch;

import com.dashur.integration.extw.connectors.parimatch.data.BalanceRequest;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceResponse;
import com.dashur.integration.extw.connectors.parimatch.data.BetRequest;
import com.dashur.integration.extw.connectors.parimatch.data.CancelRequest;
import com.dashur.integration.extw.connectors.parimatch.data.PromoRequest;
import com.dashur.integration.extw.connectors.parimatch.data.TransactionResponse;
import com.dashur.integration.extw.connectors.parimatch.data.WinRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface PariMatchClientService {

  String REST_HEADER_X_HUB_CONSUMER = "X-Hub-Consumer";

  @POST
  @Path("/slots/wallet/playerInfo")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  BalanceResponse balance(
      @HeaderParam(REST_HEADER_X_HUB_CONSUMER) String consumer, final BalanceRequest request);

  @POST
  @Path("/slots/wallet/bet")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse bet(
      @HeaderParam(REST_HEADER_X_HUB_CONSUMER) String consumer, final BetRequest request);

  @POST
  @Path("/slots/wallet/win")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse win(
      @HeaderParam(REST_HEADER_X_HUB_CONSUMER) String consumer, final WinRequest request);

  @POST
  @Path("/slots/wallet/cancel")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse cancel(
      @HeaderParam(REST_HEADER_X_HUB_CONSUMER) String consumer, final CancelRequest request);

  @POST
  @Path("/slots/wallet/transactions/promoWin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse promoWin(
      @HeaderParam(REST_HEADER_X_HUB_CONSUMER) String consumer, final PromoRequest request);
}
