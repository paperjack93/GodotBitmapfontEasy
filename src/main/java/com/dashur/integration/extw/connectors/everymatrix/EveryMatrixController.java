package com.dashur.integration.extw.connectors.everymatrix;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.domain.CommonService;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.BaseException;
import com.dashur.integration.commons.exception.DuplicateException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.exception.EntityStatusException;
import com.dashur.integration.commons.exception.PaymentException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import com.dashur.integration.extw.connectors.everymatrix.data.campaign.AwardBonusRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.campaign.BonusResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.campaign.ForfeitBonusRequest;
import com.google.common.collect.Lists;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.spi.HttpRequest;

@Slf4j
@Path("/v1/extw/exp/everymatrix")
public class EveryMatrixController {
  static final String OPERATOR_CODE = Constant.OPERATOR_EVERYMATRIX;

  @Inject ExtwIntegConfiguration config;

  @Inject ConnectorServiceLocator connectorLocator;

  @Inject Service service;

  @Inject DomainService domainService;

  @Inject CommonService commonService;

  @Context HttpRequest request;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/version")
  public String version() {
    return config.getVersion();
  }

  /**
   * Everymatrix launch game url.
   *
   * @param gameId
   * @param language
   * @param freePlay
   * @param mobile
   * @param mode
   * @param token
   * @param currencyCode
   * @param operatorId
   * @return
   */
  @GET
  @Path("/launch")
  public Response launchEveryMatrix(
      @QueryParam("operatorId") @DefaultValue("") String operatorId,
      @QueryParam("gameId") String gameId,
      @QueryParam("language") String language,
      @QueryParam("freePlay") Boolean freePlay,
      @QueryParam("mobile") Boolean mobile,
      @QueryParam("mode") String mode,
      @QueryParam("token") String token,
      @QueryParam("currencyCode") String currencyCode,
      @QueryParam("lobbyUrl") @DefaultValue("") String lobbyUrl,
      @QueryParam("bankUrl") @DefaultValue("") String bankUrl) {
    try {
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/everymatrix/launch - [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}]",
            operatorId,
            gameId,
            language,
            freePlay,
            mobile,
            mode,
            currencyCode,
            lobbyUrl,
            bankUrl);
      }
      if (freePlay) {
        if (CommonUtils.isEmptyOrNull(currencyCode)) {
          currencyCode = "USD"; // default currency for free play.
        }
        if (CommonUtils.isEmptyOrNull(language)) {
          language = "en"; // default langauge for free play
        }
        if (CommonUtils.isEmptyOrNull(token)) {
          token = null; // default langauge for free play
        }
      }

      return launchEveryMatrixInternal(
          operatorId,
          gameId,
          language,
          freePlay,
          mobile,
          mode,
          token,
          currencyCode,
          lobbyUrl,
          bankUrl);
    } catch (Exception e) {
      log.error(
          "Unable to launche game [{}] - [{}] - [{}]",
          Constant.OPERATOR_EVERYMATRIX,
          operatorId,
          gameId,
          e);
      return Response.serverError()
          .type(MediaType.TEXT_HTML)
          .encoding("utf-8")
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.launch.error.title", getLocale(language)),
                  CommonUtils.getI18nMessages("msg.launch.error.description", getLocale(language))))
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/game-state")
  @Body
  public Response playcheckEveryMatrix(
      @QueryParam("operatorId") @DefaultValue("") String operatorId, String data) {
    if (log.isDebugEnabled()) {
      log.debug("/v1/extw/exp/everymatrix/game-state - [{}] [{}]", operatorId, data);
    }

    final String language = RequestContext.instance().getLanguage();

    String roundId = "";

    try {
      EveryMatrixConfiguration.CompanySetting setting =
          validateIpAndRetrieveCompanySetting(operatorId);
      Map<String, Object> input = CommonUtils.jsonReadMap(data);

      if (!input.containsKey("RoundId")) {
        throw new ValidationException("RoundId not found");
      }

      roundId = input.get("RoundId").toString();

      String url =
          service.playcheckUrl(
              RequestContext.instance(),
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential(),
              roundId);

      return Response.ok().type(MediaType.TEXT_HTML).encoding("utf-8").entity(url).build();
    } catch (Exception e) {
      log.error(
          "Unable to launch playcheck [{}] - [{}] - [{}]", OPERATOR_CODE, operatorId, roundId, e);
      return Response.serverError()
          .type(MediaType.TEXT_HTML)
          .encoding("utf-8")
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.playcheck.error.title", getLocale(language)),
                  CommonUtils.getI18nMessages(
                      "msg.playcheck.error.description", getLocale(language))))
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/award-bonus")
  @Body
  public Response awardBonus(
      @QueryParam("operatorId") @DefaultValue("") String operatorId, String data) {
    RequestContext ctx = RequestContext.instance();
    Long itemId = 0L;
    Long vendorId = 0L;
    String campaignExtRef = null;

    try {
      EveryMatrixConfiguration.CompanySetting setting =
          validateIpAndRetrieveCompanySetting(operatorId);
      AwardBonusRequest rq = CommonUtils.jsonRead(AwardBonusRequest.class, data);

      if (Objects.isNull(rq.getGameIds())
          || rq.getGameIds().isEmpty()
          || rq.getGameIds().size() != 1) {
        throw new ValidationException("One bonus configuration only allow 1 game");
      }

      itemId = Long.parseLong(rq.getGameIds().get(0));
      campaignExtRef = String.format("%s-%s", OPERATOR_CODE, rq.getBonusId());

      ctx =
          ctx.withAccessToken(
              commonService.companyAppAccessToken(
                  ctx,
                  setting.getLauncherAppClientId(),
                  setting.getLauncherAppClientCredential(),
                  setting.getLauncherAppApiId(),
                  setting.getLauncherAppApiCredential()));
      CampaignModel campaign = null;
      try {
        campaign = domainService.searchCampaign(ctx, campaignExtRef);
      } catch (EntityNotExistException e) {
        // don't do anything.
      }

      if (Objects.isNull(campaign)) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 1);

        CampaignCreateModel create = new CampaignCreateModel();
        create.setEndTime(rq.getFreeRoundsEndDate());
        create.setGameId(itemId);
        create.setName(campaignExtRef);
        create.setNumOfGames(rq.getNumberOfFreeRounds());
        create.setExtRef(campaignExtRef);
        create.setAccountId(setting.getCompanyId());
        create.setStatus(CampaignCreateModel.Status.ACTIVE);
        create.setType(CampaignCreateModel.Type.FREE_GAMES);
        create.setBetLevel(rq.getBetValueLevel());
        create.setStartTime(now.getTime());

        campaign = domainService.createCampaign(ctx, create);
      }

      if (Objects.isNull(campaign)) {
        throw new EntityNotExistException("Campaign not exist, despite created. Please check.");
      }

      if (Objects.isNull(campaign.getVendorRef())) {
        BonusResponse resp = new BonusResponse();
        resp.setSuccess(Boolean.FALSE);
        resp.setMessage("Campaign setup is not ready. Please try again later");
        resp.setErrorCode(HttpStatus.SC_PROCESSING);
        resp.setVendorBonusId(campaign.getId().toString());
        return Response.ok()
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity(resp)
            .build();
      }

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, rq.getUserId());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%s] is not exists", rq.getUserId());
      }

      domainService.addCampaignMembers(
          ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));

      BonusResponse resp = new BonusResponse();
      resp.setSuccess(Boolean.TRUE);
      resp.setVendorBonusId(campaign.getId().toString());

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to awardBonus [{}] - [{}] - [{}]", OPERATOR_CODE, operatorId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(errorBonusResponse(e))
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/forfeit-bonus")
  @Body
  public Response forfeitBonus(
      @QueryParam("operatorId") @DefaultValue("") String operatorId, String data) {
    RequestContext ctx = RequestContext.instance();
    String campaignExtRef = null;

    try {
      EveryMatrixConfiguration.CompanySetting setting =
          validateIpAndRetrieveCompanySetting(operatorId);
      ForfeitBonusRequest rq = CommonUtils.jsonRead(ForfeitBonusRequest.class, data);
      campaignExtRef = String.format("%s-%s", OPERATOR_CODE, rq.getBonusId());

      ctx =
          ctx.withAccessToken(
              commonService.companyAppAccessToken(
                  ctx,
                  setting.getLauncherAppClientId(),
                  setting.getLauncherAppClientCredential(),
                  setting.getLauncherAppApiId(),
                  setting.getLauncherAppApiCredential()));
      CampaignModel campaign = null;
      try {
        campaign = domainService.searchCampaign(ctx, campaignExtRef);
      } catch (EntityNotExistException e) {
        // don't do anything.
      }

      if (Objects.isNull(campaign)) {
        throw new EntityNotExistException(
            "Campaign not exist, cannot forfeit award. Please check.");
      }

      if (Objects.isNull(campaign.getVendorRef())) {
        BonusResponse resp = new BonusResponse();
        resp.setSuccess(Boolean.FALSE);
        resp.setMessage("Campaign setup is not ready. Please try again later");
        resp.setErrorCode(HttpStatus.SC_PROCESSING);
        resp.setVendorBonusId(campaign.getId().toString());
        return Response.ok()
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity(resp)
            .build();
      }

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, rq.getUserId());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%s] is not exists", rq.getUserId());
      }

      domainService.delCampaignMembers(
          ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));

      BonusResponse resp = new BonusResponse();
      resp.setSuccess(Boolean.TRUE);
      resp.setVendorBonusId(campaign.getId().toString());

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to forfeitBonus [{}] - [{}] - [{}]", OPERATOR_CODE, operatorId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(errorBonusResponse(e))
          .build();
    }
  }

  /**
   * @param err
   * @return
   */
  private BonusResponse errorBonusResponse(Exception err) {
    BonusResponse response = new BonusResponse();
    response.setSuccess(Boolean.FALSE);
    if (err instanceof BaseException) {
      if (err instanceof AuthException) {
        response.setMessage("Authentication occur");
        response.setErrorCode(HttpStatus.SC_UNAUTHORIZED);
      } else if (err instanceof DuplicateException) {
        response.setMessage("Duplicate found");
        response.setErrorCode(HttpStatus.SC_CONFLICT);
      } else if (err instanceof EntityNotExistException) {
        response.setMessage("Entity not found");
        response.setErrorCode(HttpStatus.SC_NOT_FOUND);
      } else if (err instanceof EntityStatusException) {
        response.setMessage("Status issues");
        response.setErrorCode(HttpStatus.SC_EXPECTATION_FAILED);
      } else if (err instanceof PaymentException) {
        response.setMessage("Payment issues");
        response.setErrorCode(HttpStatus.SC_PAYMENT_REQUIRED);
      } else if (err instanceof ValidationException) {
        response.setMessage("Validations issues");
        response.setErrorCode(HttpStatus.SC_BAD_REQUEST);
      } else {
        response.setMessage("Un-handle exception occurs.");
        response.setErrorCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      }
    } else {
      response.setMessage("Un-handle exception occurs.");
      response.setErrorCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    return response;
  }

  /**
   * @param operatorId
   * @return
   */
  private EveryMatrixConfiguration.CompanySetting validateIpAndRetrieveCompanySetting(
      String operatorId) {
    if (Objects.isNull(config.configuration(OPERATOR_CODE))) {
      throw new ValidationException(
          "Unable to find configuration for operator [%s]", OPERATOR_CODE);
    }

    EveryMatrixConfiguration operatorConfig =
        config.configuration(OPERATOR_CODE, EveryMatrixConfiguration.class);

    Long companyId = 0L;

    if (CommonUtils.isEmptyOrNull(operatorId)) {
      companyId = operatorConfig.getDefaultCompanyId();
    } else {
      String operatorIdKey = String.format("ext-%s", operatorId);
      if (!operatorConfig.getOperatorIdMap().containsKey(operatorIdKey)) {
        throw new ValidationException("no configuration found for operator-id [%s]", operatorId);
      }
      companyId = operatorConfig.getOperatorIdMap().get(operatorIdKey);
    }

    // validate caller ip
    connectorLocator
        .getConnector(OPERATOR_CODE)
        .validateIp(companyId, CommonUtils.resolveIpAddress(this.request));

    return operatorConfig.getCompanySettings().get(companyId);
  }

  /**
   * internal method for launching item.
   *
   * @param operatorId
   * @param gameId
   * @param language
   * @param freePlay
   * @param mobile
   * @param mode
   * @param token
   * @param currencyCode
   * @return
   */
  private Response launchEveryMatrixInternal(
      String operatorId,
      String gameId,
      String language,
      Boolean freePlay,
      Boolean mobile,
      String mode,
      String token,
      String currencyCode,
      String lobbyUrl,
      String bankUrl) {
    final String operator = Constant.OPERATOR_EVERYMATRIX;

    if (Objects.isNull(config.configuration(operator))) {
      throw new ValidationException("Unable to find configuration for operator [%s]", operator);
    }

    EveryMatrixConfiguration operatorConfig =
        config.configuration(operator, EveryMatrixConfiguration.class);

    if (CommonUtils.isEmptyOrNull(mode)) {
      throw new ValidationException("mode is empty");
    }

    if (!mode.equals(operatorConfig.getMode())) {
      throw new ValidationException("mode is invalid [%s]", mode);
    }

    Long itemId = Long.parseLong(gameId);
    Long companyId = 0L;

    if (CommonUtils.isEmptyOrNull(operatorId)) {
      companyId = operatorConfig.getDefaultCompanyId();
    } else {
      String operatorIdKey = String.format("ext-%s", operatorId);
      if (!operatorConfig.getOperatorIdMap().containsKey(operatorIdKey)) {
        throw new ValidationException("no configuration found for operator-id [%s]", operatorId);
      }
      companyId = operatorConfig.getOperatorIdMap().get(operatorIdKey);
    }

    if (!operatorConfig.getCompanySettings().containsKey(companyId)) {
      throw new ValidationException("no configuration found for company-id [%s]", companyId);
    }

    EveryMatrixConfiguration.CompanySetting setting =
        operatorConfig.getCompanySettings().get(companyId);

    String url =
        service.launchUrl(
            RequestContext.instance().withCurrency(currencyCode).withLanguage(language),
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential(),
            setting.getLauncherItemApplicationId(),
            itemId,
            freePlay,
            token,
            lobbyUrl,
            bankUrl);

    URI uri = null;

    try {
      uri = new URI(url);
    } catch (URISyntaxException e) {
      log.error("Unable to convert url to uri [{}]", url);
      throw new ApplicationException("Unable to convert url to uri");
    }

    return Response.temporaryRedirect(uri).build();
  }

  /**
   * get locale for i18n.
   *
   * @param language
   * @return
   */
  private Locale getLocale(String language) {
    if (CommonUtils.isEmptyOrNull(language)) {
      return Locale.ENGLISH;
    }

    try {
      return new Locale(language);
    } catch (Exception e) {
      log.debug("Unable to resolve language, will return english");
      return Locale.ENGLISH;
    }
  }
}
