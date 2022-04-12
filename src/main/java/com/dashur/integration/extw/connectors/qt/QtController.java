package com.dashur.integration.extw.connectors.qt;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.domain.CommonService;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.exception.*;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;

@Slf4j
@Path("/v1/extw/exp/qt")
public class QtController {
  static final String OPERATOR_CODE = Constant.OPERATOR_QTECH;

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
   * launch game
   *
   * @param operatorId
   * @param gameId
   * @param language
   * @param demo
   * @param token
   * @param lobbyUrl
   * @param bankUrl
   * @return
   */
  @GET
  @Path("/launch")
  public Response launch(
      @QueryParam("operatorId") @DefaultValue("") String operatorId,
      @QueryParam("gameId") String gameId,
      @QueryParam("language") @DefaultValue("en") String language,
      @QueryParam("demo") Boolean demo,
      @QueryParam("token") String token,
      @QueryParam("lobbyUrl") @DefaultValue("") String lobbyUrl,
      @QueryParam("bankUrl") @DefaultValue("") String bankUrl) {
    try {
      if (demo) {
        if (CommonUtils.isEmptyOrNull(token)) {
          token = null; // empty token for demo
        }
      }

      String callerIp = CommonUtils.resolveIpAddress(this.request);

      return launchInternal(operatorId, gameId, language, demo, token, lobbyUrl, bankUrl, callerIp);
    } catch (Exception e) {
      log.error(
          "Unable to launche game [{}] - [{}] - [{}]",
          Constant.OPERATOR_QTECH,
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

  /**
   * @param operatorId
   * @param language
   * @param roundId
   * @param userId
   * @param gameId
   * @param hash
   * @return
   */
  @GET
  @Path("/game-state")
  public Response gamestate(
      @QueryParam("operatorId") @DefaultValue("") String operatorId,
      @QueryParam("language") @DefaultValue("") String language,
      @QueryParam("roundId") String roundId,
      @QueryParam("userId") String userId,
      @QueryParam("gameId") String gameId,
      @QueryParam("hash") String hash) {
    try {
      QtConfiguration.CompanySetting setting = retrieveCompanySetting(operatorId);
      String computedHash =
          QtConnectorServiceImpl.Utils.hash(
              operatorId,
              language,
              roundId,
              userId,
              gameId,
              ((QtConfiguration) config.configuration(OPERATOR_CODE)).getHashKey());

      if (!hash.equals(computedHash)) {
        log.warn(
            "hash is not the same [{}] - [{}] - [{}] - [{}] - [{}] - [{} vs {}]",
            operatorId,
            language,
            roundId,
            userId,
            gameId,
            hash,
            computedHash);
        throw new ValidationException("Unable to validate hashes");
      }

      if (CommonUtils.isEmptyOrNull(language)) {
        language = "en"; // default lang
      }

      String url =
          service.playcheckUrl(
              RequestContext.instance(),
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential(),
              roundId);

      URI uri = null;
      try {
        uri = new URI(url);
      } catch (URISyntaxException e) {
        log.error("Unable to convert url to uri [{}]", url);
        throw new ApplicationException("Unable to convert url to uri");
      }

      return Response.temporaryRedirect(uri).build();
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

  /**
   * @param operatorId
   * @return
   */
  private QtConfiguration.CompanySetting retrieveCompanySetting(String operatorId) {
    if (Objects.isNull(config.configuration(OPERATOR_CODE))) {
      throw new ValidationException(
          "Unable to find configuration for operator [%s]", OPERATOR_CODE);
    }

    QtConfiguration operatorConfig = config.configuration(OPERATOR_CODE, QtConfiguration.class);

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
    //
    //    // validate caller ip
    //    connectorLocator
    //        .getConnector(OPERATOR_CODE)
    //        .validateIp(companyId, CommonUtils.resolveIpAddress(this.request));

    return operatorConfig.getCompanySettings().get(companyId);
  }

  /**
   * internal method for launching item.
   *
   * @param operatorId
   * @param gameId
   * @param language
   * @param demo
   * @param token
   * @param lobbyUrl
   * @param bankUrl
   * @param callerIp
   * @return
   */
  private Response launchInternal(
      String operatorId,
      String gameId,
      String language,
      Boolean demo,
      String token,
      String lobbyUrl,
      String bankUrl,
      String callerIp) {
    if (Objects.isNull(config.configuration(OPERATOR_CODE))) {
      throw new ValidationException(
          "Unable to find configuration for operator [%s]", OPERATOR_CODE);
    }

    QtConfiguration operatorConfig = config.configuration(OPERATOR_CODE, QtConfiguration.class);

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

    QtConfiguration.CompanySetting setting = operatorConfig.getCompanySettings().get(companyId);

    String url =
        service.launchUrl(
            RequestContext.instance().withLanguage(language),
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential(),
            setting.getLauncherItemApplicationId(),
            itemId,
            demo,
            token,
            lobbyUrl,
            bankUrl,
            callerIp);

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
