package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.AccountBalanceModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.SimplifyAccountAppSettingsModel;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
@RegisterClientHeaders
public interface AccountClientService {

  @GET
  @Path("/v1/account/balance")
  @Produces(MediaType.APPLICATION_JSON)
  AccountBalanceModel balance(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang);

  @GET
  @Path("/v1/account/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<SimpleAccountModel> account(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("accountId") Long accountId);

  @GET
  @Path("/v1/account/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<SimpleAccountModel> accountByTenant(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
      @PathParam("accountId") Long accountId);

  @GET
  @Path("/v1/account")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<SimpleAccountModel>> accountsByExtRef(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @QueryParam("ext_ref") String extRef);

  @GET
  @Path("/v1/account/{accountId}/app-settings")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<SimplifyAccountAppSettingsModel>> accountAppSettings(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("accountId") Long accountId);

  @GET
  @Path("/v1/account/{accountId}/app-settings")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<SimplifyAccountAppSettingsModel>> accountAppSettingsByTenant(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
      @PathParam("accountId") Long accountId);
}
