package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
@RegisterClientHeaders
public interface FeedTransactionClientService {

  @GET
  @Path("/v1/feed/transactionround")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<TransactionRoundModel>> getTransactionRoundByExternalRef(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @QueryParam("external_ref") String externalRef);

  @GET
  @Path("/v1/feed/transactionround/{round_id}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<TransactionRoundModel> getTransactionRoundByRoundId(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("round_id") Long roundId);

  @GET
  @Path("/v1/feed/transactionround")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<TransactionRoundModel>> getTransactionRoundByAccountIdAndTime(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @QueryParam("application_id") Long applicationId,
      @QueryParam("account_id") Long accountId,
      @QueryParam("start_time") String startTime,
      @QueryParam("end_time") String endTime,
      @QueryParam("page_size") Integer pageSize,
      @QueryParam("page") Integer page);

  @GET
  @Path("/v1/feed/transaction")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<TransactionFeedModel>> getTransactionByExternalRef(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @QueryParam("external_ref") String externalRef);

  @GET
  @Path("/v1/feed/transactions/{tx_ids}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<TransactionFeedModel>> getTransactionByTxIds(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("tx_ids") String txIds);

  @GET
  @Path("/v1/feed/transaction/{transactionId}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<TransactionFeedModel> getTransactionById(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("transactionId") Long transactionId);
}
