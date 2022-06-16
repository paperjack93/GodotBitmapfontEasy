package com.dashur.integration.extw.connectors.relaxgaming;

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
import com.dashur.integration.extw.connectors.relaxgaming.data.ErrorResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.AddFreeSpinsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.DepositRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.PingResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.Request;
import com.dashur.integration.extw.connectors.relaxgaming.data.Response;
import com.dashur.integration.extw.connectors.relaxgaming.data.RollbackRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.TransactionResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.WithdrawRequest;
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
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Named("relaxgaming-connector")
@Singleton
@Slf4j
public class RelaxGamingConnectorServiceImpl implements ConnectorService {

  @Inject ExtwIntegConfiguration config;

  private RelaxGamingConfiguration relaxConfig;

  private Map<Long, RelaxGamingClientService> clientServices;

  @PostConstruct
  public void init() {
    clientServices = new ConcurrentHashMap<>();
    relaxConfig = config.configuration(Constant.OPERATOR_RELAXGAMING, RelaxGamingConfiguration.class);
  }

  @Override
  public DasAuthResponse auth(Long companyId, DasAuthRequest request) {
    try {
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      RelaxGamingClientService clientService = clientService(companyId);
      VerifyTokenRequest operatorReq = (VerifyTokenRequest) Utils.map(request, setting);
      VerifyTokenResponse operatorRes = clientService.verifyToken(auth, partnerId, operatorReq);
      return (DasAuthResponse) Utils.map(request, operatorRes);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasBalanceResponse balance(Long companyId, DasBalanceRequest request) {
    try {
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      RelaxGamingClientService clientService = clientService(companyId);
      BalanceRequest operatorReq = (BalanceRequest) Utils.map(request, setting);
      BalanceResponse operatorRes = clientService.getBalance(auth, partnerId, operatorReq);
      return (DasBalanceResponse) Utils.map(request, operatorRes);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasTransactionResponse transaction(Long companyId, DasTransactionRequest request) {
    try {
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      RelaxGamingClientService clientService = clientService(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();

      if (DasTransactionCategory.WAGER == request.getCategory()) {        
        WithdrawRequest operatorReq = (WithdrawRequest) Utils.map(request, setting);
        TransactionResponse operatorRes = clientService.withdraw(auth, partnerId, operatorReq);
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      } else if (DasTransactionCategory.PAYOUT == request.getCategory()) {
        DepositRequest operatorReq = (DepositRequest) Utils.map(request, setting);
        TransactionResponse operatorRes = clientService.deposit(auth, partnerId, operatorReq);
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      } else if (DasTransactionCategory.REFUND == request.getCategory()) {
        RollbackRequest operatorReq = (RollbackRequest) Utils.map(request, setting);
        TransactionResponse operatorRes = clientService.rollback(auth, partnerId, operatorReq);
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
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      RelaxGamingClientService clientService = clientService(companyId);
      DepositRequest operatorReq = (DepositRequest) Utils.map(request, setting);
      TransactionResponse operatorRes = clientService.deposit(auth, partnerId, operatorReq);
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

    if (!relaxConfig.getCompanySettings().containsKey(companyId)) {
      throw new ApplicationException("company [%s] config is not exists", companyId);
    }

    String hmacKey = relaxConfig.getCompanySettings().get(companyId).getHmacKey();
    String computedHmacHash = HmacUtil.hash(hmacKey, rawData);
    if (!computedHmacHash.equals(hmacHash)) {
      log.warn(
          "hmac-hash is not same with computed-hmac-hash [{} vs {}]", hmacHash, computedHmacHash);
    }
  }

  @Override
  public void validateIp(Long companyId, String callerIp) {
    if (relaxConfig.isValidateIps()) {
      if (CommonUtils.isEmptyOrNull(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP Validation is enabled, but caller ip is empty [%s]",
            callerIp);
      }

      callerIp = callerIp.trim();

      if (!relaxConfig.getWhitelistIps().contains(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP [%s] is not whitelisted", callerIp);
      }
    }
  }

  /** Retrieve clientService based on company id */
  private RelaxGamingClientService clientService(Long companyId) {
    RelaxGamingConfiguration.CompanySetting setting = relaxConfig.getCompanySettings().get(companyId);

    if (clientServices.containsKey(setting.getCompanyId())) {
      return clientServices.get(setting.getCompanyId());
    }

    try {
      String baseUri = setting.getRemoteBaseUri();
      RelaxGamingClientService clientService =
          RestClientBuilder.newBuilder()
              .baseUri(new URI(baseUri))
              .build(RelaxGamingClientService.class);
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
    static Request map(DasRequest request, RelaxGamingConfiguration.CompanySetting settings) {
      Request output;

      log.debug("map request: {}", CommonUtils.jsonToString(request));

      Map<String, Object> metaData = getMetaData(request);
      String gameRef = metaData.getOrDefault("gameRef", "").toString();
      String clientId = metaData.getOrDefault("clientId", "").toString();

      if (gameRef.isEmpty()) {
        throw new ValidationException("Unable to resolve gameRef");
      } 
      if (clientId.isEmpty()) {
        // throw new ValidationException("Unable to resolve clientId");
        log.warn("unable to resolve clientId");
      } 

      if (request instanceof DasAuthRequest) {
        String ip = metaData.getOrDefault(
          com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS, "").toString();
        if (ip.isEmpty()) {
          throw new ValidationException("Unable to resolve player ip address");
        }
        VerifyTokenRequest operatorReq = new VerifyTokenRequest();
        operatorReq.setChannel(settings.getChannel());
        operatorReq.setClientId(clientId);
        operatorReq.setToken(request.getToken());
        operatorReq.setGameRef(gameRef);
        operatorReq.setPartnerId(settings.getPartnerId());
        operatorReq.setIp(ip);     // TODO: get this from the request context?
        operatorReq.setTimestamp();
        operatorReq.setRequestId(request.getReqId());
        output = operatorReq;
      } else if (request instanceof DasBalanceRequest) {
        DasBalanceRequest balanceRequest = (DasBalanceRequest) request;
        BalanceRequest operatorReq = new BalanceRequest();
        operatorReq.setPlayerId(Integer.parseInt(balanceRequest.getAccountExtRef()));
        operatorReq.setGameRef(gameRef);
        operatorReq.setCurrency(balanceRequest.getCurrency());
        operatorReq.setSessionId(Long.parseLong(balanceRequest.getToken()));
        operatorReq.setTimestamp();
        operatorReq.setRequestId(balanceRequest.getReqId());
        output = operatorReq;
      } else if (request instanceof DasTransactionRequest) {
        DasTransactionRequest txRequest = (DasTransactionRequest) request;

        if (DasTransactionCategory.WAGER == txRequest.getCategory()) {
          WithdrawRequest operatorReq = new WithdrawRequest();
          operatorReq.setPlayerId(Integer.parseInt(txRequest.getAccountExtRef()));
          operatorReq.setRoundId(txRequest.getRoundId());
          operatorReq.setGameRef(gameRef);
          operatorReq.setChannel(settings.getChannel());
          operatorReq.setCurrency(txRequest.getCurrency());
          operatorReq.setClientId(clientId);
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setSessionId(Long.parseLong(txRequest.getToken()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).longValue());
          operatorReq.setTxType("withdraw");  // freespinbet if amount == 0
          operatorReq.setEnded(Boolean.FALSE);
          operatorReq.setTimestamp();
          operatorReq.setRequestId(txRequest.getReqId());
          output = operatorReq;

        } else if (DasTransactionCategory.PAYOUT == txRequest.getCategory()) {
          DepositRequest operatorReq = new DepositRequest();
          operatorReq.setPlayerId(Integer.parseInt(txRequest.getAccountExtRef()));
          operatorReq.setRoundId(txRequest.getRoundId());
          operatorReq.setGameRef(gameRef);
          operatorReq.setChannel(settings.getChannel());
          operatorReq.setCurrency(txRequest.getCurrency());
          operatorReq.setClientId(clientId);
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setSessionId(Long.parseLong(txRequest.getToken()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).longValue());
          operatorReq.setTxType("deposit");  // freespinpayout or freespinpayoutfinal
          operatorReq.setEnded(Boolean.FALSE);
          operatorReq.setTimestamp(); // new date().getTime());
          operatorReq.setRequestId(txRequest.getReqId());
          output = operatorReq;

        } else if (DasTransactionCategory.REFUND == txRequest.getCategory()) {
          RollbackRequest operatorReq = new RollbackRequest();
          operatorReq.setPlayerId(Integer.parseInt(txRequest.getAccountExtRef()));
          operatorReq.setRoundId(txRequest.getRoundId());
          operatorReq.setGameRef(gameRef);
          operatorReq.setCurrency(txRequest.getCurrency());
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          // operatorReq.setOriginalTxId(String.valueOf(txRequest.getTxId()));  // same as txId?
          operatorReq.setSessionId(Long.parseLong(txRequest.getToken()));
          // operatorReq.setEnded(Boolean.FALSE); // only with Paddy Power Betfair
          operatorReq.setTimestamp(); // new date().getTime());
          // operatorReq.setOriginalTimestamp(???)          
          operatorReq.setRequestId(txRequest.getReqId());
          output = operatorReq;

        } else {
          throw new ApplicationException("Unknown input, not mapped [%s] - category", request);
        }
      } else if (request instanceof DasEndRoundRequest) {
        DasEndRoundRequest endRequest = (DasEndRoundRequest) request;        
        DepositRequest operatorReq = new DepositRequest();
        operatorReq.setPlayerId(Integer.parseInt(endRequest.getAccountExtRef()));
        operatorReq.setRoundId(endRequest.getRoundId());
        operatorReq.setGameRef(gameRef);
        operatorReq.setChannel(settings.getChannel());
        operatorReq.setCurrency(endRequest.getCurrency());
        operatorReq.setClientId(clientId);
        operatorReq.setTxId(String.valueOf(endRequest.getTxId()));
        operatorReq.setSessionId(Long.parseLong(endRequest.getToken()));
        operatorReq.setAmount(0L);
        operatorReq.setTxType("deposit");  // freespinpayout or freespinpayoutfinal
        operatorReq.setEnded(Boolean.TRUE);
        operatorReq.setTimestamp(); // new date().getTime());
        operatorReq.setRequestId(endRequest.getReqId());
        output = operatorReq;        
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", request);
      }

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
        VerifyTokenResponse operatorRes = (VerifyTokenResponse) input;

        DasAuthResponse response = new DasAuthResponse();
        response.setToken(authRequest.getToken());
        response.setAccountExtRef(operatorRes.getPlayerId().toString());
        response.setBalance(
          new DasMoney(
            operatorRes.getCurrency(),
            CommonUtils.fromCents(operatorRes.getBalance().longValue())));
        if (CommonUtils.isEmptyOrNull(operatorRes.getUserName())) {
          response.setUsername("ref-" + operatorRes.getCustomerId());
        } else {
          response.setUsername(operatorRes.getUserName());
        }
        response.setCurrency(operatorRes.getCurrency());
        response.setTimestamp(new Date());
        response.setReqId(authRequest.getReqId());
        log.debug("DasAuthResponse: {}", response);
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

      if (errorRes.getCode().equals("INVALID_TOKEN")
          || errorRes.getCode().equals("BLOCKED_FROM_PRODUCT")
          || errorRes.getCode().equals("IP_BLOCKED")
          || errorRes.getCode().equals("SESSION_EXPIRED")) {
        return new AuthException(
            "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
      }

      if (errorRes.getCode().equals("INSUFFICIENT_FUNDS") ||
        errorRes.getCode().equals("SPENDING_BUDGET_EXCEEDED")) {
        return new PaymentException(
            "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
      }

      if (errorRes.getCode().equals("TRANSACTION_NOT_FOUND") ||
        errorRes.getCode().equals("INVALID_TXID")) {
        return new EntityNotExistException(
            "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
      }

      /*
       * All other error codes:
       *  INVALID_PARAMETERS => Something wrong with the request parameters.
       *  TRANSACTION_DECLINED => Operator declined the withdraw transaction. Transaction shall not be rollbacked.
       *  RC_SESSION_EXPIRED => Reality check is due.
       *  RETRY => Platform shall retry the transaction (deposit or rollback).
       *  ROLLBACK => Platform shall rollback the withdraw.
       *  MAINTENANCE => Scheduled maintenance is ongoing.
       *  UNHANDLED => Final fallback error code.
       *  SETUP_TIMEOUT => Requests are piling up.
       *  CHANNEL_MISSING => channel in data object is missing.
       *  FREESPINSID_MISSING => freespinsid in data object is missing.
       *  GAME_SESSION_MISMATCH => Returned in withdraw if sessionid in request was used for another gameref.
       *
       *  CUSTOM_ERROR => Custom error messages can be sent for example for special limits.
       */
      return new ApplicationException(
          "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
    }

    /**
     * Get the opr_meta map from a request
     *
     * @param request
     * @return
     */
    static Map<String,Object> getMetaData(DasRequest request) {
      if (Objects.nonNull(request.getCtx())) {
        return (Map<String,Object>)request.getCtx().getOrDefault(
          com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_OPR_META,
          new HashMap<>());
      }
      return new HashMap<String,Object>();
    }
  }
}
