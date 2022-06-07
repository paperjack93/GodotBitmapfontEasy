package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.SimplifyAccountAppSettingsModel;
import com.dashur.integration.commons.rest.model.SimplifyApplicationModel;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
@RegisterClientHeaders
public interface ApplicationClientService {

  @GET
  @Path("/v1/application/{applicationId}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<SimplifyApplicationModel> application(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("applicationId") Long applicationId);

  @GET
  @Path("/v1/application/{applicationId}")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<SimplifyApplicationModel> applicationByTenant(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
      @PathParam("applicationId") Long applicationId);

  @GET
  @Path("/v1/application/{applicationId}/applicationSettings")
  @Produces(MediaType.APPLICATION_JSON)
  RestResponseWrapperModel<List<SimplifyAccountAppSettingsModel>> applicationSettings(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @PathParam("applicationId") Long applicationId,
      @QueryParam("account_id") Long accountId,
      @QueryParam("enabled") Boolean enabled);
}
