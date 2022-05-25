package com.dashur.integration.extw.connectors.relaxgaming;
/*
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.BetRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.CancelRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.PromoRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.TransactionResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.WinRequest;
*/
import com.dashur.integration.extw.connectors.relaxgaming.data.AddFreeSpinsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.DepositRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.PingResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.RollbackRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.TransactionResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.WithdrawRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface RelaxGamingClientService {
  @POST
  @Path("/verifytoken")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  VerifyTokenResponse verifyToken(final VerifyTokenRequest request);

  @POST
  @Path("/withdraw")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse withdraw(final WithdrawRequest request);

  @POST
  @Path("/deposit")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse deposit(final DepositRequest request);

  @POST
  @Path("/rollback")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse rollback(final RollbackRequest request);

  @POST
  @Path("/getbalance")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  BalanceResponse getBalance(final BalanceRequest request);

  @POST
  @Path("/addfreespins")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  TransactionResponse addFreespins(final AddFreeSpinsRequest request);

  @POST
  @Path("/ping")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PingResponse ping();

}
