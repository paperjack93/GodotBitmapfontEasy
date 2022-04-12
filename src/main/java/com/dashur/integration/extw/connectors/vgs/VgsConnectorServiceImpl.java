package com.dashur.integration.extw.connectors.vgs;

import com.dashur.integration.commons.exception.*;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorService;
import com.dashur.integration.extw.connectors.HmacUtil;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemAuthResponse;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemChangeBalanceResponse;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemGetBalanceResponse;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemResponse;
import com.dashur.integration.extw.data.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Named("vgs-connector")
@Singleton
@Slf4j
public class VgsConnectorServiceImpl implements ConnectorService {
  @Inject ExtwIntegConfiguration config;
  @Inject @RestClient VgsClientService clientService;

  private VgsConfiguration configuration;

  @PostConstruct
  public void init() {
    configuration = config.configuration(Constant.OPERATOR_VGS, VgsConfiguration.class);
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
    String token = request.getToken();
    String hash = Utils.hash(configuration.getHashSecret(), request);
    String xml = clientService.authenticate(token, hash);
    VgsSystemAuthResponse operatorRs = CommonUtils.xmlRead(VgsSystemAuthResponse.class, xml);
    if (Utils.isSuccess(operatorRs)) {
      return (DasAuthResponse) Utils.map(request, operatorRs);
    }
    throw Utils.toException(operatorRs);
  }

  @Override
  public DasBalanceResponse balance(Long companyId, DasBalanceRequest request) {
    String userId = request.getAccountExtRef();
    String hash = Utils.hash(configuration.getHashSecret(), request);
    String xml = clientService.balance(userId, hash);
    VgsSystemGetBalanceResponse operatorRs =
        CommonUtils.xmlRead(VgsSystemGetBalanceResponse.class, xml);
    if (Utils.isSuccess(operatorRs)) {
      return (DasBalanceResponse) Utils.map(request, operatorRs);
    }
    throw Utils.toException(operatorRs);
  }

  @Override
  public DasTransactionResponse transaction(Long companyId, DasTransactionRequest request) {
    String userId = request.getAccountExtRef();
    BigDecimal amount = request.getAmount();
    String transactionId = String.valueOf(request.getTxId());
    String transactionType = Utils.map(request.getCategory());
    String transactionDesc = "";
    String roundId = request.getRoundId();
    String gameId = String.valueOf(request.getTxId());
    String history = "";
    Boolean isRoundFinished =
        (request.getCategory() == DasTransactionCategory.ENDROUND) ? Boolean.TRUE : Boolean.FALSE;
    String hash = Utils.hash(configuration.getHashSecret(), request);
    String xml =
        clientService.transaction(
            userId,
            amount,
            transactionId,
            transactionType,
            transactionDesc,
            roundId,
            gameId,
            history,
            isRoundFinished,
            hash);
    VgsSystemChangeBalanceResponse operatorRs =
        CommonUtils.xmlRead(VgsSystemChangeBalanceResponse.class, xml);
    if (Utils.isSuccess(operatorRs)) {
      return (DasTransactionResponse) Utils.map(request, operatorRs);
    }
    throw Utils.toException(operatorRs);
  }

  @Override
  public DasEndRoundResponse endRound(Long companyId, DasEndRoundRequest request) {
    String userId = request.getAccountExtRef();
    BigDecimal amount = BigDecimal.ZERO;
    String transactionId = String.valueOf(request.getTxId());
    String transactionType = Utils.map(DasTransactionCategory.ENDROUND);
    String transactionDesc = "";
    String roundId = request.getRoundId();
    String gameId = String.valueOf(request.getTxId());
    String history = "";
    Boolean isRoundFinished = Boolean.TRUE;
    String hash = Utils.hash(configuration.getHashSecret(), request);

    String xml =
        clientService.transaction(
            userId,
            amount,
            transactionId,
            transactionType,
            transactionDesc,
            roundId,
            gameId,
            history,
            isRoundFinished,
            hash);
    VgsSystemChangeBalanceResponse operatorRs =
        CommonUtils.xmlRead(VgsSystemChangeBalanceResponse.class, xml);
    if (Utils.isSuccess(operatorRs)) {
      DasEndRoundResponse txRs = (DasEndRoundResponse) Utils.map(request, operatorRs);
      DasEndRoundResponse rs = new DasEndRoundResponse();
      rs.setReqId(txRs.getReqId());
      rs.setBalance(txRs.getBalance());
      rs.setToken(txRs.getToken());
      rs.setTimestamp(txRs.getTimestamp());
      return rs;
    }
    throw Utils.toException(operatorRs);
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
    static final SimpleDateFormat HASH_TIME_FORMAT = new SimpleDateFormat("yyyy:MM:dd:hh");

    /**
     * check if its a succesful response.
     *
     * @param response
     * @return
     */
    static Boolean isSuccess(VgsSystemResponse response) {
      if (Objects.nonNull(response) && Objects.nonNull(response.response())) {
        return "OK".equals(response.response().getResult());
      }
      return Boolean.FALSE;
    }

    static BaseException toException(VgsSystemResponse response) {
      if (isSuccess(response)) {
        log.error(
            "try to mapped response to exception, but it should be a successful response: [{}]",
            response);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (Objects.isNull(response.response())) {
        log.error("try to mapped response to exception, response is empty: [{}]", response);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (CommonUtils.isEmptyOrNull(response.response().getCode())) {
        log.error("try to mapped response to exception, response.code is empty: [{}]", response);
        return new ApplicationException("Un-handle response mapping error.");
      }

      int errorCode;

      try {
        errorCode = Integer.parseInt(response.response().getCode());
      } catch (Exception e) {
        log.error(
            "try to mapped response to exception, response.code is not map-able: [{}]", response);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (2 == errorCode || 101 == errorCode || 102 == errorCode || 400 == errorCode) {
        return new AuthException("Connector response [%s]", errorCode);
      }

      if (109 == errorCode || 212 == errorCode || 300 == errorCode) {
        return new PaymentException("Connector response [%s]", errorCode);
      }

      return new ApplicationException("Connector response [%s]", errorCode);
    }

    static String map(DasTransactionCategory category) {
      if (Objects.isNull(category)) {
        throw new ApplicationException("Category is null");
      }

      if (DasTransactionCategory.WAGER == category) {
        return "BET";
      } else if (DasTransactionCategory.PAYOUT == category) {
        return "WIN";
      } else if (DasTransactionCategory.REFUND == category) {
        return "CANCELED_BET";
      } else if (DasTransactionCategory.ENDROUND == category) {
        return "WIN";
      }

      throw new ApplicationException("Category is null");
    }

    static DasResponse map(DasRequest request, VgsSystemResponse response) {
      DasResponse output;

      if (response instanceof VgsSystemAuthResponse) {
        DasAuthResponse output2 = new DasAuthResponse();
        DasAuthRequest request2 = (DasAuthRequest) request;
        VgsSystemAuthResponse input2 = (VgsSystemAuthResponse) response;

        output2.setToken(request.getToken());
        output2.setAccountExtRef(input2.getResponse().getUserId());
        output2.setBalance(
            new DasMoney(input2.getResponse().getCurrency(), input2.getResponse().getBalance()));
        output2.setUsername(input2.getResponse().getUsername());
        output2.setCurrency(input2.getResponse().getCurrency());
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (response instanceof VgsSystemGetBalanceResponse) {
        DasBalanceResponse output2 = new DasBalanceResponse();
        DasBalanceRequest request2 = (DasBalanceRequest) request;
        VgsSystemGetBalanceResponse input2 = (VgsSystemGetBalanceResponse) response;

        output2.setToken(request2.getToken());
        output2.setBalance(input2.getResponse().getBalance());
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (response instanceof VgsSystemChangeBalanceResponse) {
        if (request instanceof DasTransactionRequest) {
          DasTransactionResponse output2 = new DasTransactionResponse();
          DasTransactionRequest request2 = (DasTransactionRequest) request;
          VgsSystemChangeBalanceResponse input2 = (VgsSystemChangeBalanceResponse) response;

          output2.setToken(request2.getToken());
          output2.setBalance(input2.getResponse().getBalance());
          output2.setExtTxId(input2.getResponse().getEcSystemTransactionId());
          output2.setTimestamp(new Date());
          output2.setReqId(request2.getReqId());

          output = output2;
        } else if (request instanceof DasEndRoundRequest) {
          DasEndRoundResponse output2 = new DasEndRoundResponse();
          DasEndRoundRequest request2 = (DasEndRoundRequest) request;
          VgsSystemChangeBalanceResponse input2 = (VgsSystemChangeBalanceResponse) response;

          output2.setToken(request2.getToken());
          output2.setBalance(input2.getResponse().getBalance());
          output2.setTimestamp(new Date());
          output2.setReqId(request2.getReqId());

          output = output2;
        } else {
          throw new ApplicationException("Unknown input, not mapped [%s]", response);
        }
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", response);
      }

      return output;
    }

    /**
     * @param hashSecret
     * @param request
     */
    static String hash(String hashSecret, DasRequest request) {
      if (request instanceof DasAuthRequest) {
        String token = ((DasAuthRequest) request).getToken();
        return DigestUtils.md5Hex(String.format("%s%s", token, hashSecret));
      } else if (request instanceof DasBalanceRequest) {
        String userId = ((DasBalanceRequest) request).getAccountExtRef();
        return DigestUtils.md5Hex(String.format("%s%s", userId, hashSecret));
      } else if (request instanceof DasTransactionRequest) {
        String userId = ((DasTransactionRequest) request).getAccountExtRef();
        String amount = ((DasTransactionRequest) request).getAmount().toString();
        String trnType = map(((DasTransactionRequest) request).getCategory());
        String trnDesc = "";
        String roundId = ((DasTransactionRequest) request).getRoundId();
        String gameId = String.valueOf(((DasTransactionRequest) request).getTxId());
        String history = "";

        return DigestUtils.md5Hex(
            String.format(
                "%s%s%s%s%s%s%s%s",
                userId, amount, trnType, trnDesc, roundId, gameId, history, hashSecret));
      } else if (request instanceof DasEndRoundRequest) {
        String userId = ((DasEndRoundRequest) request).getAccountExtRef();
        String amount = BigDecimal.ZERO.toString();
        String trnType = map(DasTransactionCategory.PAYOUT);
        String trnDesc = "";
        String roundId = ((DasEndRoundRequest) request).getRoundId();
        String gameId = String.valueOf(((DasEndRoundRequest) request).getTxId());
        String history = "";

        return DigestUtils.md5Hex(
            String.format(
                "%s%s%s%s%s%s%s%s",
                userId, amount, trnType, trnDesc, roundId, gameId, history, hashSecret));
      }

      throw new ApplicationException("Unable to produce sign-hashes");
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

      return DigestUtils.md5Hex(
          String.format("%s%s%s%s%s%s", operatorId, language, roundId, userId, gameId, hashKey));
    }
  }
}
