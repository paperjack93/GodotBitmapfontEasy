package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.commons.rest.CampaignClientService;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.CampaignBetLevelModel;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.domain.CommonService;
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
import com.dashur.integration.extw.connectors.relaxgaming.data.service.FreeRound;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.AddFreeRoundsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.AddFreeRoundsResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetFreeRoundsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetFreeRoundsResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.CancelFreeRoundsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.CancelFreeRoundsResponse;
import com.dashur.integration.extw.rgs.RgsService;
import com.dashur.integration.extw.rgs.RgsServiceProvider;
import com.dashur.integration.extw.rgs.data.GameHash;
import com.dashur.integration.extw.rgs.data.PlaycheckExtRequest;
import com.dashur.integration.extw.rgs.data.PlaycheckExtResponse;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import java.util.UUID;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.math.BigDecimal;
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
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@Path("/v1/extw/exp/relaxgaming")
public class RelaxGamingController {
  static final String OPERATOR_CODE = Constant.OPERATOR_RELAXGAMING;
  static final String AUTHORIZATION = "Authorization";
  static final String ROUND_PREFIX = "1040-";
  static final String DEFAULT_CURRENCY = "EUR";

  @Inject ExtwIntegConfiguration config;

  @Inject ConnectorServiceLocator connectorLocator;

  @Inject Service service;

  @Inject DomainService domainService;

  @Inject CommonService commonService;

  @Inject RgsService rgsService;

  @Context HttpRequest request;

  @Inject @RestClient CampaignClientService campaignClientService;

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

      if (CommonUtils.isEmptyOrNull(clientId)) {
        clientId = "mobile_app";
        log.info("setting client if to {}", clientId);
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

    List<GameHash> rgsResp = getRgs().gameHashes("EUR");
    List<GameInfo> games = new ArrayList<GameInfo>();
    GetGamesResponse resp = new GetGamesResponse();
    for ( GameHash hash : rgsResp) {
      if (!hash.getItemId().isEmpty()) {
        GameInfo game = new GameInfo();
        game.setGameRef(getGameRef(hash.getItemId()));
        game.setName(hash.getName());
        game.setStudio(relaxConfig.getRgsProvider());
        List<Integer> legalBetSizes = new ArrayList<Integer>();
        for (Float stake : hash.getStakes().get("EUR")){
          double bet = stake.doubleValue() * 100;
          legalBetSizes.add((int)bet);
        }
        game.setLegalBetSizes(legalBetSizes);
        games.add(game);
      }
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
          "/v1/extw/exp/relaxgaming/round/getstate - [{}] [{}] [{}]",
          request.getCredentials(),
          request.getRoundId(),
          request.getJurisdiction());
    }

    GetStateResponse resp = new GetStateResponse();

    String partnerId = String.valueOf(request.getCredentials().getPartnerId());
    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

    RequestContext ctx = RequestContext.instance();
    ctx = ctx.withAccessToken(
          commonService.companyAppAccessToken(
              ctx, 
              setting.getLauncherAppClientId(), 
              setting.getLauncherAppClientCredential(), 
              setting.getLauncherAppApiId(), 
              setting.getLauncherAppApiCredential()));

    TransactionRoundModel round = domainService.findTransactionRoundByRoundExtRef(ctx, 
      getPrefixedRoundId(request.getRoundId()));


    if (!round.getMetaData().containsKey("item_id")) {
      return Response.serverError().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    }
    resp.setClosedTime(round.getCloseTime().toString());
    resp.setGameRef(getGameRef(round.getMetaData().get("item_id").toString()));
    resp.setTotalWinAmount(round.getSumOfPayout().longValue());
    return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
  }


  @POST
  @Path("/playcheck")
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
            "/v1/extw/exp/relaxgaming/playcheck - [{}] [{}]",
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
              getPrefixedRoundId(request.getRoundId()));

      GetReplayResponse resp = new GetReplayResponse();
      resp.setReplayUrl(url);
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to get playcheck [{}] - [{}]", 
        request.getCredentials().getPartnerId(), request.getRoundId(), e);
        return Response.status(500).build();
    }
  }


  @POST
  @Path("/replay/get")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getReplay(
    @HeaderParam(AUTHORIZATION) String auth, final GetReplayRequest request) {
    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/replay/get - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getRoundId());
      }

      String partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      RequestContext ctx = RequestContext.instance();
      ctx = ctx.withAccessToken(
            commonService.companyAppAccessToken(
                ctx, 
                setting.getLauncherAppClientId(), 
                setting.getLauncherAppClientCredential(), 
                setting.getLauncherAppApiId(), 
                setting.getLauncherAppApiCredential()));

      TransactionRoundModel round = domainService.findTransactionRoundByRoundExtRef(ctx, 
        getPrefixedRoundId(request.getRoundId()));
      TransactionFeedModel feed = domainService.findTransactionFeedById(ctx, round.getId());
      
      PlaycheckExtRequest playcheckReq = new PlaycheckExtRequest();
      List<TransactionFeedModel> feeds = new ArrayList<TransactionFeedModel>();
      feeds.add(feed);
      playcheckReq.setFeeds(feeds);

      PlaycheckExtResponse playcheckResp = getRgs().playcheckExt(playcheckReq);

      GetReplayResponse resp = new GetReplayResponse();
      resp.setReplayUrl(playcheckResp.getUrl());
      resp.setRoundStart(toZonedDateTime(round.getStartTime()));
      resp.setRoundEnd(toZonedDateTime(round.getCloseTime()));
      resp.setBetAmount(round.getSumOfWager().longValue());
      resp.setWinAmount(round.getSumOfPayout().longValue());
      resp.setCurrency(round.getCurrencyUnit());
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to get replay [{}] - [{}]", 
        request.getCredentials().getPartnerId(), request.getRoundId(), e);
        return Response.status(500).build();
    }
  }


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/freespins/add")
  public Response addFreespins(
      @HeaderParam(AUTHORIZATION) String auth, final AddFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    Long itemId = 0L;
    Long vendorId = 0L;
    String campaignExtRef = null;
    String campaignId = null;
    String partnerId = null;
    String currency = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/add - [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getGameRef(),
            request.getPlayerId());
      }

      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting =
          getCompanySettings(partnerId, true);

      currency = request.getCurrency();
      if (Strings.isNullOrEmpty(currency)) {
        currency = DEFAULT_CURRENCY;
        log.info("defaulting currency to {}", currency);
      }

      itemId = getItemId(request.getGameRef());
      campaignId = request.getTxId().toString(); // UUID.randomUUID().toString());
      campaignExtRef = String.format("%s-%s", OPERATOR_CODE, campaignId);
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/add - [{}] [{}] [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getPlayerId(),
            currency,
            itemId,
            campaignExtRef);
      }          

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

        RestResponseWrapperModel<List<String>> currencyResp = campaignClientService.currency(
          CommonUtils.authorizationBearer(ctx.getAccessToken()),
          ctx.getTimezone(),
          ctx.getCurrency(),
          ctx.getUuid().toString(),
          ctx.getLanguage(),
          itemId);
        if (!currencyResp.getData().contains(currency)) {
          throw new ValidationException("game %s does not accept %s as campaign currency", itemId, currency);
        }

        RestResponseWrapperModel<CampaignBetLevelModel> betlevelResp = campaignClientService.betLevel(
          CommonUtils.authorizationBearer(ctx.getAccessToken()),
          ctx.getTimezone(),
          ctx.getCurrency(),
          ctx.getUuid().toString(),
          ctx.getLanguage(),
          itemId,
          currency);

        int level = 0;
        for (BigDecimal amount : betlevelResp.getData().getLevels()) {
          if (amount.multiply(BigDecimal.valueOf(100L)).longValue() == request.getFreespinValue()) {
            break;
          }
          level++;
        }
        if (level >= betlevelResp.getData().getLevels().size()) {
          throw new ValidationException("bet amount of %f is not a valid level: %s", 
            (double)request.getFreespinValue()/100.0, 
            betlevelResp.getData().getLevels().toString());
        }

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 1);

        CampaignCreateModel create = new CampaignCreateModel();
        create.setEndTime(toDate(request.getExpires()));
        create.setGameId(itemId);
        create.setName(campaignExtRef);
        create.setNumOfGames(request.getAmount());
        create.setExtRef(campaignExtRef);
        create.setAccountId(setting.getCompanyId());
        create.setStatus(CampaignCreateModel.Status.ACTIVE);
        create.setType(CampaignCreateModel.Type.FREE_GAMES);
        create.setBetLevel(level);
        create.setCurrency(currency);
        create.setStartTime(now.getTime());

        campaign = domainService.createCampaign(ctx, create);
      }

      if (Objects.isNull(campaign)) {
        throw new EntityNotExistException("Campaign not exist, despite created. Please check.");
      }

      /*
      if (Objects.isNull(campaign.getVendorRef())) {
        log.info("Campaign setup is not ready. Please try again later");
        AddFreeRoundsResponse resp = new AddFreeRoundsResponse();
        resp.setFreespinsId(campaign.getId().toString());
        return Response.serverError()
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity(resp)
            .build();
      }
      */

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, request.getPlayerId().toString());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%d] does not exists", request.getPlayerId());
      }

      domainService.addCampaignMembers(
          ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));

      AddFreeRoundsResponse resp = new AddFreeRoundsResponse();
      resp.setTxId(request.getTxId());
      resp.setFreespinsId(campaignId);

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to addFreespins [{}] - [{}] - [{}]", OPERATOR_CODE, partnerId, e);
      return Response.serverError()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/freespins/get")
  public Response getFreespins(
      @HeaderParam(AUTHORIZATION) String auth, final GetFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    String campaignExtRef = null;
    String partnerId = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/get - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getPlayerId());
      }
      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting =
          getCompanySettings(partnerId, true);

      ctx =
          ctx.withAccessToken(
              commonService.companyAppAccessToken(
                  ctx,
                  setting.getLauncherAppClientId(),
                  setting.getLauncherAppClientCredential(),
                  setting.getLauncherAppApiId(),
                  setting.getLauncherAppApiCredential()));

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, request.getPlayerId().toString());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%d] does not exists", request.getPlayerId());
      }

      List<CampaignModel> campaigns = null;
      try {
        campaigns = domainService.availableCampaigns(ctx, memberAccount.getId());
      } catch (EntityNotExistException e) {
        // don't do anything.
      }

      GetFreeRoundsResponse resp = new GetFreeRoundsResponse();
      if (Objects.nonNull(campaigns)) {
        List<FreeRound> freeRounds = new ArrayList<FreeRound>();
        for (CampaignModel m : campaigns) {
          FreeRound r = new FreeRound();
          r.setFreespinValue(m.getBetLevel().longValue());
          r.setExpires(toZonedDateTime(m.getEndTime()));
          r.setPromoCode(m.getExtRef());
          r.setGameRef(getGameRef(m.getGameId().toString()));
          r.setAmount(m.getNumOfGames());
          r.setFreespinsId(m.getId().toString());
          r.setCreateTime(toZonedDateTime(m.getCreated()));
          r.setCurrency(m.getCurrency());
          freeRounds.add(r);
        }
        resp.setFreespins(freeRounds);
      }
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to getFreespins [{}] - [{}  ] - [{}]", OPERATOR_CODE, partnerId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/freespins/cancel")
  public Response cancelFreespins(
      @HeaderParam(AUTHORIZATION) String auth, final CancelFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    String campaignExtRef = null;
    String partnerId = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/cancel - [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getFreespinsId(),
            request.getPlayerId());
      }      
      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting =
          getCompanySettings(partnerId, true);
      campaignExtRef = String.format("%s-%s", OPERATOR_CODE, request.getFreespinsId());

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
            "Campaign not exist, cannot cancel freespins. Please check.");
      }

      if (Objects.isNull(campaign.getVendorRef())) {
        CancelFreeRoundsResponse resp = new CancelFreeRoundsResponse();
        resp.setFreespinsId(campaign.getId().toString());
        return Response.serverError()
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity(resp)
            .build();
      }

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, request.getPlayerId().toString());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%s] does not exists", request.getPlayerId());
      }

      domainService.delCampaignMembers(
          ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));

      CancelFreeRoundsResponse resp = new CancelFreeRoundsResponse();
      resp.setFreespinsId(campaign.getId().toString());

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to cancelFreespins [{}] - [{}] - [{}]", OPERATOR_CODE, partnerId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
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

    Boolean isDemo = mode.equals("fun");

    RequestContext ctx = RequestContext.instance()
                                       .withLanguage(language);
    if (isDemo) {
      if (Strings.isNullOrEmpty(demoCurrency)) {
        demoCurrency = DEFAULT_CURRENCY;
        log.info("defaulting currency to {}", demoCurrency);
      }
      log.info("launching in demo mode with currency {}", demoCurrency);
      ctx = ctx.withCurrency(demoCurrency);
    }
    ctx.getMetaData().put("clientId", clientId);
    ctx.getMetaData().put("gameRef", getGameRef(gameId));
    log.debug("launcher request context: {}", ctx.getMetaData());

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
      log.error("Basic authentication failed. Invalid credentials.");
      return false;
    }
    return true;
  }

  /**
   * toZonedDateTime
   * 
   * @param Date
   * @return ZonedDateTime in UTC
   */
  private ZonedDateTime toZonedDateTime(Date date) {
    return ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
  }

  /**
   * toDate
   * 
   * @param zonedDateTime
   * @return Date
   */
  private Date toDate(ZonedDateTime zonedDateTime) {
    return Date.from(zonedDateTime.toInstant());
  }

  /**
   * getGameRef
   * 
   * @param gameId
   * @return RelaxGaming gameRef string
   */
  private String getGameRef(String gameId) {
    return String.format("rlx.%s.%s.%s", 
      relaxConfig.getPlatform(), 
      relaxConfig.getGamestudio(),
      gameId);
  }

  /**
   * getItemId
   * 
   * @param gameRef
   * @return Dashur itemId
   */
  private Long getItemId(String gameRef) {
    String[] parts = gameRef.split("\\.");
    if (parts.length > 0) {
      return Long.parseLong(parts[parts.length-1]);
    }
    throw new ValidationException("gameRef is malformed [%s]", gameRef);
  }

  /**
   * getPrefixedRoundId
   * 
   * @param roundId
   * @return Dashur roundId
   */
  private String getPrefixedRoundId(String roundId) {
    return ROUND_PREFIX + roundId;
  }

  /**
   * getRgs
   * 
   * @return rgs service provider
   */
  private RgsServiceProvider getRgs() {
    return rgsService.getProvider(relaxConfig.getRgsProvider());
  }

}
