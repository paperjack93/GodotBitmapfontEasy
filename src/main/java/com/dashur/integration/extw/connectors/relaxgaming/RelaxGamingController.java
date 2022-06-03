package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GameInfo;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.Credentials;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.ServiceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetGamesResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetReplayRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetReplayResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetStateRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetStateResponse;
import com.dashur.integration.extw.rgs.RgsService;
import com.dashur.integration.extw.rgs.data.GameHash;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;

@Slf4j
@Path("/v1/extw/exp/relaxgaming")
public class RelaxGamingController {
  static final String OPERATOR_CODE = Constant.OPERATOR_RELAXGAMING;
  static final String AUTHORIZATION = "Authorization";

  @Inject ExtwIntegConfiguration config;

  @Inject ConnectorServiceLocator connectorLocator;

  @Inject Service service;

  @Inject RgsService rgsService;

  @Context HttpRequest request;

  private RelaxGamingConfiguration relaxConfig;

  @PostConstruct
  public void init() {
    relaxConfig = config.configuration(OPERATOR_CODE, RelaxGamingConfiguration.class);
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/version")
  public String version() {
    return config.getVersion();
  }

  /**
   * RelaxGaming launch game url
   *
   * @param gameid
   * @param ticket
   * @param jurisdiction
   * @param lang
   * @param channel
   * @param partnerid
   * @param moneymode
   * @param currency
   * @param clientid
   * @param homeurl
   * @param hidehome
   * @param fullscreen
   * @param plurl
   * @param rg_account_uri
   * @param accountlabel
   * @param sessiontimer
   * @param sessionresult
   * @param sessiontimelimit
   * @param sessionlapsed
   * @param sessionrcinterval
   * @param sessionwagered
   * @param sessionwon
   * @param sessionlostlimit
   * @param sessiontimewarninglimit
   * @param sessionlosswarninglimit
   * @param sessionshowsummary
   * @param sessiontimer
   * @param sessionresult
   * @return
   */
  @GET
  @Path("/launch")
  public Response getLauncher(
      @QueryParam("gameid") String gameId,
      @QueryParam("ticket") String token,
      @QueryParam("lang") String language,
      @QueryParam("channel") String channel,
      @QueryParam("partnerid") String partnerId,
      @QueryParam("moneymode") String mode,
      @QueryParam("currency") String demoCurrency,
      @QueryParam("clientid") String clientId,
      @QueryParam("homeurl") @DefaultValue("") String lobbyUrl) {
    try {
      String callerIp = CommonUtils.resolveIpAddress(this.request);

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/launch - [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}]",
            gameId,
            token,
            language,
            channel,
            partnerId,
            mode,
            demoCurrency,
            clientId,
            lobbyUrl,
            callerIp);
      }

      return getLauncherInternal(
        gameId, 
        token, 
        language, 
        channel, 
        partnerId, 
        mode, 
        demoCurrency,
        clientId, 
        lobbyUrl,
        callerIp);
    } catch (Exception e) {
      log.error("Unable to launch game [{}] - [{}]", gameId, partnerId, e);
      return Response.serverError()
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.launch.error.title", getLocale(language)),
                  CommonUtils.getI18nMessages("msg.launch.error.description", getLocale(language))))
          .build();
    }
  }

  @POST
  @Path("/games/getgames")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getGames(
    @HeaderParam(AUTHORIZATION) String auth, final ServiceRequest request) {
    if (!authenticate(auth, request.getCredentials().getPartnerId())) {
      return Response.status(401).build();
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "/v1/extw/exp/relaxgaming/games/getgames - [{}] [{}]",
          request.getCredentials(),
          request.getJurisdiction());
    }

    String partnerId = String.valueOf(request.getCredentials().getPartnerId());
    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, false);

    List<GameHash> rgsResp = rgsService.getProvider(relaxConfig.getRgsProvider()).gameHashes();
    List<GameInfo> games = new ArrayList<GameInfo>();
    GetGamesResponse resp = new GetGamesResponse();
    for ( GameHash hash : rgsResp) {
      GameInfo game = new GameInfo();
      game.setGameRef(getGameRef(hash.getItemId()));
      game.setName(hash.getName());
      game.setStudio(relaxConfig.getRgsProvider());

      games.add(game);
    }
    resp.setGames(games);

    return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
  }

  @POST
  @Path("/round/getstate")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getState(
    @HeaderParam(AUTHORIZATION) String auth, final GetStateRequest request) {
    if (!authenticate(auth, request.getCredentials().getPartnerId())) {
      return Response.status(401).build();
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "/v1/extw/exp/relaxgaming/state/getstate - [{}] [{}] [{}]",
          request.getCredentials(),
          request.getRoundId(),
          request.getJurisdiction());
    }

    String partnerId = String.valueOf(request.getCredentials().getPartnerId());
    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, false);

    GetStateResponse resp = new GetStateResponse();
    return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
  }


  @POST
  @Path("/replay/get")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getPlaycheck(
    @HeaderParam(AUTHORIZATION) String auth, final GetReplayRequest request) {
    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/game-state - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getRoundId());
      }

      String partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      String url =
          service.playcheckUrl(
              RequestContext.instance(),
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential(),
              request.getRoundId());

      GetReplayResponse resp = new GetReplayResponse();
      resp.setReplayUrl(url);
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to get playcheck [{}] - [{}]", 
        request.getCredentials().getPartnerId(), request.getRoundId(), e);
        return Response.status(500).build();
    }
  }

  /**
   * Internal method for launching game
   *
   * @param gameId
   * @param token
   * @param language
   * @param channel
   * @param partnerId
   * @param mode
   * @param demoCurrency
   * @param clientId
   * @param lobbyUrl
   * @param callerIp
   * @return
   */
  private Response getLauncherInternal(
      String gameId,
      String token,
      String language,
      String channel,
      String partnerId,
      String mode,
      String demoCurrency,
      String clientId,
      String lobbyUrl,
      String callerIp) {

    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, false);
    if (!channel.equals(setting.getChannel())) {
      throw new ValidationException("channel [%s] is invalid for partnerId [%s]", channel, partnerId);
    }

    Boolean isDemo = mode == "fun";

    Map<String, Object> metaData = new HashMap<String, Object>();
    metaData.put("clientId", clientId);
    metaData.put("gameRef", getGameRef(gameId));
    RequestContext ctx = RequestContext.instance()
                                       .withLanguage(language)
                                       .withMetaData(metaData);
    if (isDemo) {
      ctx = ctx.withCurrency(demoCurrency);
    }

    String url =
        service.launchUrl(
            ctx,
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential(),
            setting.getLauncherItemApplicationId(),
            Long.parseLong(gameId),
            isDemo,
            token,
            lobbyUrl,
            null,
            callerIp);

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
   * @param partnerId
   * @param validateIp
   * @return
   */
  private RelaxGamingConfiguration.CompanySetting getCompanySettings(
      String partnerId, boolean validateIp) {
    String operatorIdKey = String.format("ext-%s", partnerId);
    if (!relaxConfig.getOperatorIdMap().containsKey(operatorIdKey)) {
      throw new ValidationException("no configuration found for operator-id [%s]", partnerId);
    }

    Long companyId = relaxConfig.getOperatorIdMap().get(operatorIdKey);

    if (!relaxConfig.getCompanySettings().containsKey(companyId)) {
      throw new ValidationException("no configuration found for company-id [%s]", companyId);
    }

    if (validateIp) {
      connectorLocator
          .getConnector(OPERATOR_CODE)
          .validateIp(companyId, CommonUtils.resolveIpAddress(this.request));
    }

    return relaxConfig.getCompanySettings().get(companyId);
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

  /**
   * validate request
   * 
   * @param credentials
   * @return
   */
  private boolean authenticate(String auth, Integer partnerId) {
    if (!getCompanySettings(String.valueOf(partnerId), false).getOperatorCredential().equals(auth)) {
//      throw new ValidationException("Basic authentication failed. Invalid credentials.")
      log.error("Basic authentication failed. Invalid credentials. want [{}] got [{}]", 
        getCompanySettings(String.valueOf(partnerId), false).getOperatorCredential(), auth);
      return false;
    }
    return true;
  }

  /**
   * getGameRef
   * 
   * @param gameId
   * @return
   */
  private String getGameRef(String gameId) {
    return String.format("rlx.%s.%s.%s", 
      relaxConfig.getPlatform(), 
      relaxConfig.getGamestudio(),
      gameId);
  }
}
