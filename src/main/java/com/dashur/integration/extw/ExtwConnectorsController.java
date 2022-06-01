package com.dashur.integration.extw;

import com.dashur.integration.commons.exception.*;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import com.dashur.integration.extw.data.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.spi.HttpRequest;
import io.quarkus.logging.Log; 

/**
 * 1. This controller are not meant to be open up to public, so its a controller that only be used
 * to transtlate ext-wallet call to operator system. 2. The contoller can support for multi operator
 * integration It will handle all the necessary incoming mapping from dashur ext-wallet call.
 * However Individual necessary operator mapping will be handled on the backing service.
 */
@Slf4j
@Path("/v1/extw/connect")
public class ExtwConnectorsController {
  @Inject ExtwIntegConfiguration config;

  @Inject ConnectorServiceLocator connectorLocator;

  @Context HttpRequest request;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/version")
  public String version() {
    return config.getVersion();
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{operator}/{companyId}/v1/{api}")
  @Body
  public Response api(
      @HeaderParam("X-DAS-HMAC") String hmacHash,
      @PathParam("operator") String operator,
      @PathParam("companyId") Long companyId,
      @PathParam("api") String api,
      String data) {

    Date start = new Date();
    connectorLocator.getConnector(operator).validate(companyId, hmacHash, data);
    DasResponse response;
    DasRequest request = null;

    log.trace("input : [{} - {} - {} - {}]", operator, companyId, api, data);
//    System.out.format("input : [%s - %s - %s - %s]", operator, companyId, api, data);

    try {
      switch (api) {
        case Constant.API_AUTH:
          {
            request = CommonUtils.jsonRead(DasAuthRequest.class, data);
            response =
                connectorLocator.getConnector(operator).auth(companyId, (DasAuthRequest) request);
            break;
          }
        case Constant.API_BALANCE:
          {
            request = CommonUtils.jsonRead(DasBalanceRequest.class, data);
            response =
                connectorLocator
                    .getConnector(operator)
                    .balance(companyId, (DasBalanceRequest) request);
            break;
          }
        case Constant.API_TRANSACTION:
          {
            request = CommonUtils.jsonRead(DasTransactionRequest.class, data);
            response =
                connectorLocator
                    .getConnector(operator)
                    .transaction(companyId, (DasTransactionRequest) request);
            break;
          }
        case Constant.API_ENDROUND:
          {
            request = CommonUtils.jsonRead(DasEndRoundRequest.class, data);
            response =
                connectorLocator
                    .getConnector(operator)
                    .endRound(companyId, (DasEndRoundRequest) request);
            break;
          }
        default:
          throw new ApplicationException("Unable to find api: [%s]", api);
      }
    } catch (Exception e) {
      log.error("ExtwConnectorsController.api [{} - {} - {}]", operator, companyId, api, e);
      return error(e, start, request);
    }

    if (Objects.nonNull(response)) {
      response.setProcessingTime(getProcessingTime(start));
      return Response.ok(CommonUtils.jsonToString(response)).build();
    } else {
      return error(new ApplicationException("Response is not available here"), start, request);
    }
  }

  Response error(Exception e, Date start, DasRequest request) {
    String reqId = "";
    String token = null;
    if (Objects.isNull(request) || CommonUtils.isEmptyOrNull(request.getReqId())) {
      reqId = UUID.randomUUID().toString();
    } else {
      reqId = request.getReqId();
    }
    if (!(Objects.isNull(request) || CommonUtils.isEmptyOrNull(request.getToken()))) {
      token = request.getToken();
    }

    Response.Status status = null;

    if (e instanceof ApplicationException) {
      status = Response.Status.INTERNAL_SERVER_ERROR;
    } else if (e instanceof AuthException) {
      status = Response.Status.UNAUTHORIZED;
    } else if (e instanceof DuplicateException) {
      status = Response.Status.CONFLICT;
    } else if (e instanceof EntityNotExistException) {
      status = Response.Status.NOT_FOUND;
    } else if (e instanceof EntityStatusException) {
      status = Response.Status.CONFLICT;
    } else if (e instanceof PaymentException) {
      status = Response.Status.PAYMENT_REQUIRED;
    } else if (e instanceof ValidationException) {
      status = Response.Status.BAD_REQUEST;
    } else {
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    DasResponse resp = new DasResponse();
    resp.setReqId(reqId);
    resp.setToken(token);
    resp.setTimestamp(new Date());
    resp.setProcessingTime(getProcessingTime(start));

    return Response.status(status).entity(CommonUtils.jsonToString(resp)).build();
  }

  /**
   * Get processing time.
   *
   * @param start
   * @return
   */
  Integer getProcessingTime(Date start) {
    if (Objects.isNull(start)) {
      return 0; // unable to count. return 0
    }

    return Integer.parseInt((new Date().getTime() - start.getTime()) + "");
  }
}
