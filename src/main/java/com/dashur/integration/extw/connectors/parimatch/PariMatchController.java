package com.dashur.integration.extw.connectors.parimatch;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;

@Slf4j
@Path("/v1/extw/exp/parimatch")
public class PariMatchController {
  static final String OPERATOR_CODE = Constant.OPERATOR_PARIMATCH;

  @Inject ExtwIntegConfiguration config;

  @Inject ConnectorServiceLocator connectorLocator;

  @Inject Service service;

  @Context HttpRequest request;

  private PariMatchConfiguration pariConfig;

  @PostConstruct
  public void init() {
    pariConfig = config.configuration(OPERATOR_CODE, PariMatchConfiguration.class);
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/version")
  public String version() {
    return config.getVersion();
  }

  /**
   * PariMatch launch game url
   *
   * @param casinoId
   * @param consumerId
   * @param gameId
   * @param token
   * @param language
   * @param platform
   * @param lobbyUrl
   * @return
   */
  @GET
  @Path("/launch")
  public Response getLauncher(
      @QueryParam("cid") String casinoId,
      @QueryParam("consumerId") String consumerId,
      @QueryParam("productId") String gameId,
      @QueryParam("sessionToken") String token,
      @QueryParam("lang") String language,
      @QueryParam("targetChannel") String platform,
      @QueryParam("lobbyUrl") @DefaultValue("") String lobbyUrl) {
    try {
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/parimatch/launch - [{}] [{}] [{}] [{}] [{}] [{}]",
            casinoId,
            consumerId,
            gameId,
            language,
            platform,
            lobbyUrl);
      }

      return getLauncherInternal(casinoId, consumerId, gameId, token, language, lobbyUrl);
    } catch (Exception e) {
      log.error("Unable to launch game [{}] - [{}] - [{}]", casinoId, consumerId, gameId, e);
      return Response.serverError()
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.launch.error.title", getLocale(language)),
                  CommonUtils.getI18nMessages("msg.launch.error.description", getLocale(language))))
          .build();
    }
  }

  @GET
  @Path("/game-state")
  public Response getPlaycheck(
      @QueryParam("cid") String casinoId,
      @QueryParam("providerId") @DefaultValue("") String providerId,
      @QueryParam("roundId") String roundId,
      @QueryParam("lang") @DefaultValue("en") String language) {
    try {
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/parimatch/game-state - [{}] [{}] [{}] [{}]",
            casinoId,
            providerId,
            roundId,
            language);
      }

      PariMatchConfiguration.CompanySetting setting = getCompanySettings(providerId, true);

      String url =
          service.playcheckUrl(
              RequestContext.instance(),
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential(),
              roundId);

      return Response.temporaryRedirect(new URI(url)).build();
    } catch (Exception e) {
      log.error("Unable to get playcheck [{}] - [{}] - [{}]", casinoId, providerId, roundId, e);
      return Response.serverError()
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.playcheck.error.title", getLocale(language)),
                  CommonUtils.getI18nMessages(
                      "msg.playcheck.error.description", getLocale(language))))
          .build();
    }
  }

  /**
   * Internal method for launching game
   *
   * @param casinoId
   * @param consumerId
   * @param gameId
   * @param token
   * @param language
   * @param lobbyUrl
   * @return
   */
  private Response getLauncherInternal(
      String casinoId,
      String consumerId,
      String gameId,
      String token,
      String language,
      String lobbyUrl) {
    if (!casinoId.equals(pariConfig.getCasinoId())) {
      throw new ValidationException("casino-id is invalid [%s]", casinoId);
    }

    PariMatchConfiguration.CompanySetting setting = getCompanySettings(consumerId, false);

    String url =
        service.launchUrl(
            RequestContext.instance().withLanguage(language),
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential(),
            setting.getLauncherItemApplicationId(),
            Long.parseLong(gameId),
            CommonUtils.isEmptyOrNull(token),
            token,
            lobbyUrl,
            null);

    try {
      return Response.temporaryRedirect(new URI(url)).build();
    } catch (URISyntaxException e) {
      log.error("Unable to convert url to uri [{}]", url);
      throw new ApplicationException("Unable to convert url to uri");
    }
  }

  /**
   * get operator's company settings
   *
   * @param consumerId
   * @param validateIp
   * @return
   */
  private PariMatchConfiguration.CompanySetting getCompanySettings(
      String consumerId, boolean validateIp) {
    String operatorIdKey = String.format("ext-%s", consumerId);
    if (!pariConfig.getOperatorIdMap().containsKey(operatorIdKey)) {
      throw new ValidationException("no configuration found for operator-id [%s]", consumerId);
    }

    Long companyId = pariConfig.getOperatorIdMap().get(operatorIdKey);

    if (!pariConfig.getCompanySettings().containsKey(companyId)) {
      throw new ValidationException("no configuration found for company-id [%s]", companyId);
    }

    if (validateIp) {
      connectorLocator
          .getConnector(OPERATOR_CODE)
          .validateIp(companyId, CommonUtils.resolveIpAddress(this.request));
    }

    return pariConfig.getCompanySettings().get(companyId);
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
      log.debug("Unable to resolve language, return default 'en'");
      return Locale.ENGLISH;
    }
  }
}
