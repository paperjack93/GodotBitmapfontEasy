package com.dashur.integration.extw.connectors.parimatch;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.BaseException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.exception.PaymentException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorService;
import com.dashur.integration.extw.connectors.HmacUtil;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceRequest;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceResponse;
import com.dashur.integration.extw.connectors.parimatch.data.BetRequest;
import com.dashur.integration.extw.connectors.parimatch.data.CancelRequest;
import com.dashur.integration.extw.connectors.parimatch.data.ErrorResponse;
import com.dashur.integration.extw.connectors.parimatch.data.PromoRequest;
import com.dashur.integration.extw.connectors.parimatch.data.Request;
import com.dashur.integration.extw.connectors.parimatch.data.Response;
import com.dashur.integration.extw.connectors.parimatch.data.TransactionResponse;
import com.dashur.integration.extw.connectors.parimatch.data.WinRequest;
import com.dashur.integration.extw.data.DasAuthRequest;
import com.dashur.integration.extw.data.DasAuthResponse;
import com.dashur.integration.extw.data.DasBalanceRequest;
import com.dashur.integration.extw.data.DasBalanceResponse;
import com.dashur.integration.extw.data.DasEndRoundRequest;
import com.dashur.integration.extw.data.DasEndRoundResponse;
import com.dashur.integration.extw.data.DasMoney;
import com.dashur.integration.extw.data.DasRequest;
import com.dashur.integration.extw.data.DasResponse;
import com.dashur.integration.extw.data.DasTransactionCategory;
import com.dashur.integration.extw.data.DasTransactionRequest;
import com.dashur.integration.extw.data.DasTransactionResponse;
import com.google.common.collect.Maps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Named("parimatch-connector")
@Singleton
@Slf4j
public class PariMatchConnectorServiceImpl implements ConnectorService {

  @Inject ExtwIntegConfiguration config;

  private PariMatchConfiguration pariConfig;

  private Map<Long, PariMatchClientService> clientServices;

  @PostConstruct
  public void init() {
//    clientServices = Maps.newConcurrentMap();
    clientServices = new ConcurrentHashMap<>();
    pariConfig = config.configuration(Constant.OPERATOR_PARIMATCH, PariMatchConfiguration.class);
  }

  @Override
  public DasAuthResponse auth(Long companyId, DasAuthRequest request) {
    try {
      PariMatchConfiguration.CompanySetting setting =
          pariConfig.getCompanySettings().get(companyId);
      PariMatchClientService clientService = clientService(companyId);
      BalanceRequest operatorReq = (BalanceRequest) Utils.map(request, pariConfig.getCasinoId());
      BalanceResponse operatorRes = clientService.balance(setting.getConsumerId(), operatorReq);
      return (DasAuthResponse) Utils.map(request, operatorRes);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasBalanceResponse balance(Long companyId, DasBalanceRequest request) {
    try {
      PariMatchConfiguration.CompanySetting setting =
          pariConfig.getCompanySettings().get(companyId);
      PariMatchClientService clientService = clientService(companyId);
      BalanceRequest operatorReq = (BalanceRequest) Utils.map(request, pariConfig.getCasinoId());
      BalanceResponse operatorRes = clientService.balance(setting.getConsumerId(), operatorReq);
      return (DasBalanceResponse) Utils.map(request, operatorRes);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasTransactionResponse transaction(Long companyId, DasTransactionRequest request) {
    try {
      PariMatchConfiguration.CompanySetting setting =
          pariConfig.getCompanySettings().get(companyId);
      PariMatchClientService clientService = clientService(companyId);

      if (DasTransactionCategory.WAGER == request.getCategory()) {
        BetRequest operatorReq = (BetRequest) Utils.map(request, pariConfig.getCasinoId());
        TransactionResponse operatorRes = clientService.bet(setting.getConsumerId(), operatorReq);
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      } else if (DasTransactionCategory.PAYOUT == request.getCategory()) {
        WinRequest operatorReq = (WinRequest) Utils.map(request, pariConfig.getCasinoId());
        TransactionResponse operatorRes;

        // Operator requires separate endpoint for campaign payout
        if (Objects.nonNull(request.getCampaignId())) {
          PromoRequest promoReq = new PromoRequest(operatorReq);
          operatorRes = clientService.promoWin(setting.getConsumerId(), promoReq);
        } else {
          operatorRes = clientService.win(setting.getConsumerId(), operatorReq);
        }

        return (DasTransactionResponse) Utils.map(request, operatorRes);
      } else if (DasTransactionCategory.REFUND == request.getCategory()) {
        CancelRequest operatorReq = (CancelRequest) Utils.map(request, pariConfig.getCasinoId());
        TransactionResponse operatorRes =
            clientService.cancel(setting.getConsumerId(), operatorReq);
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      }
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }

    throw new ApplicationException(
        "Unable to find handler for the tx - category => %s", request.getCategory());
  }

  @Override
  public DasEndRoundResponse endRound(Long companyId, DasEndRoundRequest request) {
    try {
      PariMatchConfiguration.CompanySetting setting =
          pariConfig.getCompanySettings().get(companyId);
      PariMatchClientService clientService = clientService(companyId);
      WinRequest operatorReq = (WinRequest) Utils.map(request, pariConfig.getCasinoId());
      TransactionResponse operatorRes = clientService.win(setting.getConsumerId(), operatorReq);
      return (DasEndRoundResponse) Utils.map(request, operatorRes);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public void validate(Long companyId, String hmacHash, String rawData) {
    if (CommonUtils.isEmptyOrNull(hmacHash)) {
      throw new ApplicationException("hmac-hash is empty");
    }

    if (Objects.isNull(companyId)) {
      throw new ApplicationException("companyId is empty");
    }

    if (CommonUtils.isEmptyOrNull(rawData)) {
      throw new ApplicationException("rawData is empty");
    }

    if (!pariConfig.getCompanySettings().containsKey(companyId)) {
      throw new ApplicationException("company [%s] config is not exists", companyId);
    }

    String hmacKey = pariConfig.getCompanySettings().get(companyId).getHmacKey();
    String computedHmacHash = HmacUtil.hash(hmacKey, rawData);

    if (!computedHmacHash.equals(hmacHash)) {
      log.warn(
          "hmac-hash is not same with computed-hmac-hash [{} vs {}]", hmacHash, computedHmacHash);
    }
  }

  @Override
  public void validateIp(Long companyId, String callerIp) {
    if (pariConfig.isValidateIps()) {
      if (CommonUtils.isEmptyOrNull(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP Validation is enabled, but caller ip is empty [%s]",
            callerIp);
      }

      callerIp = callerIp.trim();

      if (!pariConfig.getWhitelistIps().contains(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP [%s] is not whitelisted", callerIp);
      }
    }
  }

  /** Retrieve clientService based on company id */
  private PariMatchClientService clientService(Long companyId) {
    PariMatchConfiguration.CompanySetting setting = pariConfig.getCompanySettings().get(companyId);

    if (clientServices.containsKey(setting.getCompanyId())) {
      return clientServices.get(setting.getCompanyId());
    }

    try {
      String baseUri = setting.getRemoteBaseUri();
      PariMatchClientService clientService =
          RestClientBuilder.newBuilder()
              .baseUri(new URI(baseUri))
              .build(PariMatchClientService.class);
      clientServices.put(setting.getCompanyId(), clientService);

      return clientServices.get(setting.getCompanyId());
    } catch (URISyntaxException e) {
      log.error("Unable to construct clientService", e);
      throw new ApplicationException("Unable to create client service.");
    }
  }

  /** Utility classes */
  static final class Utils {

    /**
     * Map Dashur request object to Operator request object
     *
     * @param request
     * @param casinoId
     * @return
     */
    static Request map(DasRequest request, String casinoId) {
      Request output;

      if (request instanceof DasAuthRequest || request instanceof DasBalanceRequest) {
        BalanceRequest operatorReq = new BalanceRequest();
        operatorReq.setToken(request.getToken());
        output = operatorReq;
      } else if (request instanceof DasTransactionRequest) {
        DasTransactionRequest txRequest = (DasTransactionRequest) request;

        if (DasTransactionCategory.WAGER == txRequest.getCategory()) {
          BetRequest operatorReq = new BetRequest();
          operatorReq.setToken(txRequest.getToken());
          operatorReq.setPlayerId(txRequest.getAccountExtRef());
          operatorReq.setGameId(String.valueOf(txRequest.getItemId()));
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).intValue());
          operatorReq.setRoundId(txRequest.getRoundId());
          operatorReq.setRoundClosed(Boolean.FALSE);
          operatorReq.setCurrency(txRequest.getCurrency());
          output = operatorReq;
        } else if (DasTransactionCategory.PAYOUT == txRequest.getCategory()) {
          WinRequest operatorReq = new WinRequest();
          operatorReq.setPlayerId(txRequest.getAccountExtRef());
          operatorReq.setGameId(String.valueOf(txRequest.getItemId()));
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).intValue());
          operatorReq.setRoundId(txRequest.getRoundId());
          operatorReq.setRoundClosed(Boolean.FALSE);
          operatorReq.setCurrency(txRequest.getCurrency());
          output = operatorReq;
        } else if (DasTransactionCategory.REFUND == txRequest.getCategory()) {
          CancelRequest operatorReq = new CancelRequest();
          operatorReq.setPlayerId(txRequest.getAccountExtRef());
          operatorReq.setGameId(String.valueOf(txRequest.getItemId()));
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).intValue());
          operatorReq.setRoundId(txRequest.getRoundId());
          operatorReq.setCurrency(txRequest.getCurrency());
          output = operatorReq;
        } else {
          throw new ApplicationException("Unknown input, not mapped [%s] - category", request);
        }
      } else if (request instanceof DasEndRoundRequest) {
        DasEndRoundRequest endRequest = (DasEndRoundRequest) request;
        WinRequest operatorReq = new WinRequest();
        operatorReq.setPlayerId(endRequest.getAccountExtRef());
        operatorReq.setGameId(String.valueOf(endRequest.getItemId()));
        operatorReq.setTxId(endRequest.getTxId() + "-end");
        operatorReq.setAmount(0);
        operatorReq.setRoundId(endRequest.getRoundId());
        operatorReq.setRoundClosed(Boolean.TRUE);
        operatorReq.setCurrency(endRequest.getCurrency());
        output = operatorReq;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", request);
      }

      output.setCasinoId(casinoId);
      return output;
    }

    /**
     * Map Operator response object to Dashur response object
     *
     * @param request
     * @param input
     * @return
     */
    static DasResponse map(DasRequest request, Response input) {
      DasResponse output;

      if (request instanceof DasAuthRequest) {
        DasAuthRequest authRequest = (DasAuthRequest) request;
        BalanceResponse operatorRes = (BalanceResponse) input;

        DasAuthResponse response = new DasAuthResponse();
        response.setToken(authRequest.getToken());
        response.setAccountExtRef(operatorRes.getPlayerId());
        response.setBalance(
            new DasMoney(
                operatorRes.getCurrency(),
                CommonUtils.fromCents(operatorRes.getBalance().longValue())));
        response.setCountry(operatorRes.getCountry());
        response.setUsername(operatorRes.getPlayerName());
        response.setCurrency(operatorRes.getCurrency());
        response.setTimestamp(new Date());
        response.setReqId(authRequest.getReqId());
        output = response;
      } else if (request instanceof DasBalanceRequest) {
        DasBalanceRequest balRequest = (DasBalanceRequest) request;
        BalanceResponse operatorRes = (BalanceResponse) input;

        DasBalanceResponse response = new DasBalanceResponse();
        response.setToken(balRequest.getToken());
        response.setBalance(CommonUtils.fromCents(operatorRes.getBalance().longValue()));
        response.setTimestamp(new Date());
        response.setReqId(balRequest.getReqId());
        output = response;
      } else if (request instanceof DasTransactionRequest) {
        DasTransactionRequest txRequest = (DasTransactionRequest) request;
        TransactionResponse operatorRes = (TransactionResponse) input;

        DasTransactionResponse response = new DasTransactionResponse();
        response.setToken(txRequest.getToken());
        response.setBalance(CommonUtils.fromCents(operatorRes.getBalance().longValue()));
        response.setExtTxId(operatorRes.getExtwTxId());
        response.setTimestamp(new Date());
        response.setReqId(txRequest.getReqId());
        output = response;
      } else if (request instanceof DasEndRoundRequest) {
        DasEndRoundRequest endRequest = (DasEndRoundRequest) request;
        TransactionResponse operatorRes = (TransactionResponse) input;

        DasEndRoundResponse response = new DasEndRoundResponse();
        response.setToken(endRequest.getToken());
        response.setBalance(CommonUtils.fromCents(operatorRes.getBalance().longValue()));
        response.setTimestamp(new Date());
        response.setReqId(endRequest.getReqId());
        output = response;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", input);
      }

      return output;
    }

    /**
     * Map Operator exception object to Dashur exception object
     *
     * @param ex
     * @return
     */
    static BaseException toException(WebApplicationException ex) {
      if (Objects.isNull(ex) || Objects.isNull(ex.getResponse())) {
        log.error("error mapping exception. response is null", ex);
        return new ApplicationException("Unhandled exception mapping.");
      }

      if (!ex.getResponse().hasEntity()) {
        log.error(
            "error mapping exception. response doesn't contain body - status: [{}]",
            ex.getResponse().getStatus());
        return new ApplicationException("Unhandled exception mapping.");
      }

      ErrorResponse errorRes = ex.getResponse().readEntity(ErrorResponse.class);

      if (Objects.isNull(errorRes) || Objects.isNull(errorRes.getCode())) {
        log.error(
            "error mapping exception. response body is empty - status: [{}]",
            ex.getResponse().getStatus());
        return new ApplicationException("Unhandled exception mapping.");
      }

      if (errorRes.getCode().equals("invalid.session.key")
          || errorRes.getCode().equals("locked.player")) {
        return new AuthException(
            "Connector response [%s] - [%s]", errorRes.getCode(), errorRes.getMessage());
      }

      if (errorRes.getCode().equals("insufficient.balance")) {
        return new PaymentException(
            "Connector response [%s] - [%s]", errorRes.getCode(), errorRes.getMessage());
      }

      if (errorRes.getCode().equals("invalid.transaction.id")) {
        return new EntityNotExistException(
            "Connector response [%s] - [%s]", errorRes.getCode(), errorRes.getMessage());
      }

      /*
       * All other error codes:
       * => invalid.casino.logic - returned for unexpected casino game flow
       * => request.timeout - returned due to service unavailability
       * => integration.hub.failure - returned for wrong input values provided to hub
       * => error.internal - unexpected error
       */
      return new ApplicationException(
          "Connector response [%s] - [%s]", errorRes.getCode(), errorRes.getMessage());
    }
  }
}
