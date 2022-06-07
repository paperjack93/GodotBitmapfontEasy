package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.AppIdModel;
import com.dashur.integration.commons.rest.model.LoginMemberModel;
import com.dashur.integration.commons.rest.model.MemberTokenModel;
import com.dashur.integration.commons.rest.model.TokenModel;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.FormParam;

@Path("/")
@RegisterRestClient
@RegisterClientHeaders
public interface AuthClientService {
  @GET
  @Path("app-id.json")
  @Produces(MediaType.APPLICATION_JSON)
  AppIdModel authAppId();

  @POST
  @Path("/oauth/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  TokenModel refreshToken(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @FormParam(Constant.REST_AUTH_PARAM_GRANT_TYPE) String grantType,
      @FormParam(Constant.REST_AUTH_PARAM_CLIENT_ID) String clientId,
      @FormParam(Constant.REST_AUTH_PARAM_REFRESH_TOKEN) String refreshToken);

  @POST
  @Path("/oauth/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  TokenModel loginPassword(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @javax.ws.rs.FormParam(Constant.REST_AUTH_PARAM_GRANT_TYPE) String grantType,
      @javax.ws.rs.FormParam(Constant.REST_AUTH_PARAM_USERNAME) String username,
      @javax.ws.rs.FormParam(Constant.REST_AUTH_PARAM_PASSWORD) String password);

  @POST
  @Path("/oauth/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  TokenModel loginAppClient(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @FormParam(Constant.REST_AUTH_PARAM_GRANT_TYPE) String grantType,
      @FormParam(Constant.REST_AUTH_PARAM_CLIENT_ID) String clientId,
      @FormParam(Constant.REST_AUTH_PARAM_CLIENT_SECRET) String clientSecret);

  @POST
  @Path("/v1/token/login_as_member")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  MemberTokenModel loginAsMember(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      final LoginMemberModel model);
}
