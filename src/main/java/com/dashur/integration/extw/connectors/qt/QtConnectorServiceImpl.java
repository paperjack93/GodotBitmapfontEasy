package com.dashur.integration.extw.connectors.qt;

import com.dashur.integration.commons.exception.*;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.commons.utils.CurrencyUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorService;
import com.dashur.integration.extw.connectors.HmacUtil;
import com.dashur.integration.extw.connectors.qt.data.*;
import com.dashur.integration.extw.data.*;
import com.google.common.collect.Maps;
import com.google.common.collect.MapMaker;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Named("qt-connector")
@Singleton
@Slf4j
public class QtConnectorServiceImpl implements ConnectorService {
  @Inject ExtwIntegConfiguration config;
  private Map<Long, QtClientService> clientServices;
  private QtConfiguration configuration;

  @PostConstruct
  public void init() {
//    clientServices = Maps.newConcurrentMap();
    clientServices = new ConcurrentHashMap<>();
    configuration = config.configuration(Constant.OPERATOR_QTECH, QtConfiguration.class);
  }

  /**
   * retrieve clientService base on companyId.
   *
   * @param companyId
   * @return
   */
  protected QtClientService clientService(Long companyId) {
    QtConfiguration.CompanySetting setting = setting(companyId);

    if (clientServices.containsKey(setting.getCompanyId())) {
      return clientServices.get(setting.getCompanyId());
    }

    String baseUri = setting(companyId).getRemoteBaseUri();
    try {
      QtClientService clientService =
          org.eclipse.microprofile.rest.client.RestClientBuilder.newBuilder()
              .baseUri(new URI(baseUri))
              .build(QtClientService.class);

      clientServices.put(setting.getCompanyId(), clientService);

      return clientServices.get(setting.getCompanyId());
    } catch (URISyntaxException e) {
      log.error("Unable to construct clientService", e);
      throw new ApplicationException("Unable to create client service.");
    }
  }

  protected QtConfiguration.CompanySetting setting(Long companyId) {
    if (Objects.nonNull(companyId)) {
      if (configuration.getCompanySettings().containsKey(companyId)) {
        return configuration.getCompanySettings().get(companyId);
      }
    }

    return configuration.getDefaultCompanySetting();
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

    if (!configuration.getCompanySettings().containsKey(companyId)) {
      throw new ApplicationException("company [%s] config is not exists", companyId);
    }

    String hmacKey = configuration.getCompanySettings().get(companyId).getHmacKey();
    String computedHmacHash = HmacUtil.hash(hmacKey, rawData);

    if (!computedHmacHash.equals(hmacHash)) {
      log.warn(
          "hmac-hash is not same with computed-hmac-hash [{} vs {}]", hmacHash, computedHmacHash);
    }
  }

  @Override
  public DasAuthResponse auth(Long companyId, DasAuthRequest request) {
    try {
      AuthRequest operatorRq = (AuthRequest) Utils.map(request);
      AuthResponse operatorRs =
          clientService(companyId)
              .authenticate(
                  setting(companyId).getPassKey(),
                  request.getToken(),
                  configuration.getProviderId(),
                  operatorRq);
      return (DasAuthResponse) Utils.map(request, operatorRs);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasBalanceResponse balance(Long companyId, DasBalanceRequest request) {
    try {
      BalanceResponse operatorRs =
          clientService(companyId)
              .balance(
                  setting(companyId).getPassKey(),
                  request.getToken(),
                  configuration.getProviderId(),
                  request.getAccountExtRef());
      return (DasBalanceResponse) Utils.map(request, operatorRs);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasTransactionResponse transaction(Long companyId, DasTransactionRequest request) {
    if (DasTransactionCategory.WAGER == request.getCategory()) {
      try {
        WithdrawalRequest operatorRq = (WithdrawalRequest) Utils.map(request);
        WithdrawalResponse operatorRs =
            clientService(companyId)
                .withdrawal(
                    setting(companyId).getPassKey(),
                    request.getToken(),
                    configuration.getProviderId(),
                    operatorRq);
        return (DasTransactionResponse) Utils.map(request, operatorRs);
      } catch (WebApplicationException e) {
        throw Utils.toException(e);
      }
    } else if (DasTransactionCategory.PAYOUT == request.getCategory()) {
      try {
        DepositRequest operatorRq = (DepositRequest) Utils.map(request);
        DepositResponse operatorRs =
            clientService(companyId)
                .deposit(
                    setting(companyId).getPassKey(),
                    request.getToken(),
                    configuration.getProviderId(),
                    operatorRq);
        return (DasTransactionResponse) Utils.map(request, operatorRs);
      } catch (WebApplicationException e) {
        throw Utils.toException(e);
      }
    } else if (DasTransactionCategory.REFUND == request.getCategory()) {
      try {
        RollbackRequest operatorRq = (RollbackRequest) Utils.map(request);
        RollbackResponse operatorRs =
            clientService(companyId)
                .rollback(
                    setting(companyId).getPassKey(),
                    request.getToken(),
                    configuration.getProviderId(),
                    operatorRq);
        return (DasTransactionResponse) Utils.map(request, operatorRs);
      } catch (WebApplicationException e) {
        throw Utils.toException(e);
      }
    } else {
      throw new ApplicationException(
          "Unable to find handler for the tx - category => %s", request.getCategory());
    }
  }

  @Override
  public DasEndRoundResponse endRound(Long companyId, DasEndRoundRequest request) {
    try {
      DepositRequest operatorRq = (DepositRequest) Utils.map(request);
      DepositResponse operatorRs =
          clientService(companyId)
              .deposit(
                  setting(companyId).getPassKey(),
                  request.getToken(),
                  configuration.getProviderId(),
                  operatorRq);
      return (DasEndRoundResponse) Utils.map(request, operatorRs);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public void validateIp(Long companyId, String callerIp) {
    if (configuration.isValidateIps()) {
      if (CommonUtils.isEmptyOrNull(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP Validation is enabled, but caller ip is empty [%s]",
            callerIp);
      }

      callerIp = callerIp.trim();

      if (!configuration.getWhitelistIps().contains(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP [%s] is not whitelisted", callerIp);
      }
    }
  }

  /** Utility classes */
  static final class Utils {
    static BaseException toException(WebApplicationException err) {
      if (Objects.isNull(err) || Objects.isNull(err.getResponse())) {
        log.error("try to mapped exception, however either err is empty or response is empty", err);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (!err.getResponse().hasEntity()) {
        log.error(
            "try to mapped exception, however err response doesn't contain body. Treat as generic error: [{}]",
            err.getResponse().getStatus(),
            err);
        return new ApplicationException("Un-handle response mapping error.");
      }

      Response jsonRs = err.getResponse().readEntity(Response.class);

      if (Objects.isNull(jsonRs)) {
        log.error(
            "try to mapped response to exception, error body is empty: [{} => {}]",
            err.getResponse().getStatus(),
            jsonRs);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (CommonUtils.isEmptyOrNull(jsonRs.getCode())) {
        log.error("try to mapped response to exception, error body is empty: [{} => {}]", jsonRs);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if ("INVALID_TOKEN".equals(jsonRs.getCode())
          || "GAME_MISMATCH".equals(jsonRs.getCode())
          || "IP_MISMATCH".equals(jsonRs.getCode())
          || "ACCOUNT_BLOCKED".equals(jsonRs.getCode())
          || "LOGIN_FAILED".equals(jsonRs.getCode())
          || "PLAYER_MISMATCH".equals(jsonRs.getCode())) {
        return new AuthException(
            "Connector response [%s] - [%s] - [%s]",
            err.getResponse().getStatus(), jsonRs.getCode(), jsonRs.getMessage());
      }

      if ("INSUFFICIENT_FUNDS".equals(jsonRs.getCode())
          || "LIMIT_EXCEEDED".equals(jsonRs.getCode())) {
        return new PaymentException(
            "Connector response [%s] - [%s] - [%s]",
            err.getResponse().getStatus(), jsonRs.getCode(), jsonRs.getMessage());
      }

      return new ApplicationException(
          "Connector response [%s] - [%s] - [%s]",
          err.getResponse().getStatus(), jsonRs.getCode(), jsonRs.getMessage());
    }

    /**
     * Map Dashur request object into EveryMatrix request Objec.t
     *
     * @param input
     * @return
     */
    static Request map(DasRequest input) {
      Request output;
      if (input instanceof DasAuthRequest) {
        AuthRequest output2 = new AuthRequest();

        if (Objects.nonNull(input.getCtx())) {
          if (input
              .getCtx()
              .containsKey(
                  com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_OPR_META)) {
            if (input
                    .getCtx()
                    .getOrDefault(
                        com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_OPR_META,
                        new HashMap<>())
//                        Maps.newHashMap())
                instanceof Map) {
              Map<String, Object> oprMeta =
                  (Map<String, Object>)
                      input
                          .getCtx()
                          .getOrDefault(
                              com.dashur.integration.commons.Constant
                                  .LAUNCHER_META_DATA_KEY_OPR_META,
                              Maps.newHashMap());
              output2.setGameId(
                  oprMeta
                      .getOrDefault(
                          com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_GAME_ID,
                          "")
                      .toString());
              output2.setIpAddress(
                  oprMeta
                      .getOrDefault(
                          com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS,
                          "")
                      .toString());
            }
          }
        }

        if (CommonUtils.isEmptyOrNull(output2.getGameId())) {
          throw new ValidationException("Unable to resolved game-id");
        }

        if (CommonUtils.isEmptyOrNull(output2.getIpAddress())) {
          throw new ValidationException("Unable to resolved ip-addr");
        }

        output = output2;
      } else if (input instanceof DasBalanceRequest) {
        output = new Request();
      } else if (input instanceof DasTransactionRequest) {
        DasTransactionRequest input2 = (DasTransactionRequest) input;
        if (DasTransactionCategory.WAGER == ((DasTransactionRequest) input).getCategory()) {
          WithdrawalRequest output2 = new WithdrawalRequest();

          output2.setCompleted(Boolean.FALSE);
          output2.setCurrency(input2.getCurrency());
          output2.setAmount(input2.getAmount());
          output2.setTxnId(String.valueOf(input2.getTxId()));
          output2.setGameId(String.valueOf(input2.getItemId()));
          output2.setRoundId(input2.getRoundId());
          output2.setCreated(input2.getTimestamp());
          output2.setPlayerId(input2.getAccountExtRef());
          // TODO: handle jackpot here

          output = output2;
        } else if (DasTransactionCategory.PAYOUT == ((DasTransactionRequest) input).getCategory()) {
          DepositRequest output2 = new DepositRequest();

          output2.setCurrency(input2.getCurrency());
          output2.setAmount(input2.getAmount());
          output2.setTxnId(String.valueOf(input2.getTxId()));
          output2.setGameId(String.valueOf(input2.getItemId()));
          output2.setRoundId(input2.getRoundId());
          output2.setCompleted(Boolean.FALSE);
          output2.setBetId(input2.getRoundId());
          output2.setCreated(input2.getTimestamp());
          output2.setPlayerId(input2.getAccountExtRef());

          output = output2;
        } else if (DasTransactionCategory.REFUND == ((DasTransactionRequest) input).getCategory()) {
          RollbackRequest output2 = new RollbackRequest();

          output2.setCurrency(input2.getCurrency());
          output2.setAmount(input2.getAmount());
          output2.setTxnId(String.valueOf(input2.getTxId()));
          output2.setGameId(String.valueOf(input2.getItemId()));
          output2.setRoundId(input2.getRoundId());
          output2.setCompleted(Boolean.FALSE);
          output2.setBetId(String.valueOf(input2.getRefundTxId()));
          output2.setCreated(input2.getTimestamp());
          output2.setPlayerId(input2.getAccountExtRef());

          output = output2;
        } else {
          throw new ApplicationException("Unknown input, not mapped [%s] - category", input);
        }
      } else if (input instanceof DasEndRoundRequest) {
        DepositRequest output2 = new DepositRequest();
        DasEndRoundRequest input2 = (DasEndRoundRequest) input;

        output2.setCurrency(input2.getCurrency());
        output2.setAmount(BigDecimal.ZERO);
        output2.setTxnId(String.valueOf(input2.getTxId()) + "-end");
        output2.setGameId(String.valueOf(input2.getItemId()));
        output2.setRoundId(input2.getRoundId());
        output2.setCompleted(Boolean.TRUE);
        output2.setBetId(input2.getRoundId());
        output2.setCreated(input2.getTimestamp());
        output2.setPlayerId(input2.getAccountExtRef());

        output = output2;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", input);
      }

      return output;
    }

    static DasResponse map(DasRequest request, Response input) {
      DasResponse output;

      if (request instanceof DasAuthRequest && input instanceof AuthResponse) {
        DasAuthResponse output2 = new DasAuthResponse();
        DasAuthRequest request2 = (DasAuthRequest) request;
        AuthResponse input2 = (AuthResponse) input;

        output2.setToken(request.getToken());
        output2.setAccountExtRef(input2.getPlayerId());
        output2.setBalance(
            new DasMoney(
                input2.getCurrency(),
                CurrencyUtils.ensureAmountScaleByCurrency(
                    input2.getCurrency(), input2.getBalance())));
        output2.setCountry(input2.getCountry());
        output2.setUsername(input2.getPlayerId() + "-" + input2.getScreenName());
        output2.setCurrency(input2.getCurrency());
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());
        output2.setLang(input2.getLanguage());

        output = output2;
      } else if (request instanceof DasBalanceRequest && input instanceof BalanceResponse) {
        DasBalanceResponse output2 = new DasBalanceResponse();
        DasBalanceRequest request2 = (DasBalanceRequest) request;
        BalanceResponse input2 = (BalanceResponse) input;

        output2.setToken(request2.getToken());
        output2.setBalance(
            CurrencyUtils.ensureAmountScaleByCurrency(input2.getCurrency(), input2.getBalance()));
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (request instanceof DasTransactionRequest) {
        DasTransactionRequest request2 = (DasTransactionRequest) request;
        DasTransactionResponse output2 = new DasTransactionResponse();
        if (DasTransactionCategory.WAGER == ((request2.getCategory()))
            && input instanceof WithdrawalResponse) {
          WithdrawalResponse input2 = (WithdrawalResponse) input;
          output2.setToken(request2.getToken());
          output2.setBalance(
              CurrencyUtils.ensureAmountScaleByCurrency(
                  request2.getCurrency(), input2.getBalance()));
          output2.setExtTxId(input2.getReferenceId());
          output2.setTimestamp(new Date());
          output2.setReqId(request2.getReqId());
        } else if (DasTransactionCategory.PAYOUT == ((request2.getCategory()))
            && input instanceof DepositResponse) {
          DepositResponse input2 = (DepositResponse) input;
          output2.setToken(request2.getToken());
          output2.setBalance(
              CurrencyUtils.ensureAmountScaleByCurrency(
                  request2.getCurrency(), input2.getBalance()));
          output2.setExtTxId(input2.getReferenceId());
          output2.setTimestamp(new Date());
          output2.setReqId(request2.getReqId());
        } else if (DasTransactionCategory.REFUND == ((request2.getCategory()))
            && input instanceof RollbackResponse) {
          RollbackResponse input2 = (RollbackResponse) input;
          output2.setToken(request2.getToken());
          output2.setBalance(
              CurrencyUtils.ensureAmountScaleByCurrency(
                  request2.getCurrency(), input2.getBalance()));
          output2.setExtTxId(input2.getReferenceId());
          output2.setTimestamp(new Date());
          output2.setReqId(request2.getReqId());
        } else {
          // if category == endround, should issues proper end-round request
          throw new ApplicationException("Unhandled category [%s]", request2.getCategory());
        }
        output = output2;
      } else if (request instanceof DasEndRoundRequest && input instanceof DepositResponse) {
        DasEndRoundResponse output2 = new DasEndRoundResponse();
        DasEndRoundRequest request2 = (DasEndRoundRequest) request;
        DepositResponse input2 = (DepositResponse) input;
        output2.setToken(request2.getToken());
        output2.setBalance(
            CurrencyUtils.ensureAmountScaleByCurrency(request2.getCurrency(), input2.getBalance()));
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());
        output = output2;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", request);
      }

      return output;
    }

    static final String hash(
        String operatorId,
        String language,
        String roundId,
        String userId,
        String gameId,
        String hashKey) {
      if (CommonUtils.isEmptyOrNull(operatorId)) {
        operatorId = "";
      }
      if (CommonUtils.isEmptyOrNull(language)) {
        language = "";
      }
      if (CommonUtils.isEmptyOrNull(roundId)) {
        roundId = "";
      }
      if (CommonUtils.isEmptyOrNull(userId)) {
        userId = "";
      }
      if (CommonUtils.isEmptyOrNull(gameId)) {
        gameId = "";
      }
      if (CommonUtils.isEmptyOrNull(hashKey)) {
        hashKey = "";
      }

      return DigestUtils.sha256Hex(
          String.format("%s%s%s%s%s%s", operatorId, language, roundId, userId, gameId, hashKey));

      //      return DigestUtils.md5Hex(
      //          String.format("%s%s%s%s%s%s", operatorId, language, roundId, userId, gameId,
      // hashKey));
    }
  }
}
