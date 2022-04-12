package com.dashur.integration.extw.connectors.everymatrix;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.BaseException;
import com.dashur.integration.commons.exception.DuplicateException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.exception.PaymentException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorService;
import com.dashur.integration.extw.connectors.HmacUtil;
import com.dashur.integration.extw.connectors.everymatrix.data.AuthenticateRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.AuthenticateResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.BalanceRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.BalanceResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.BetRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.BetResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.CancelRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.CancelResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.Request;
import com.dashur.integration.extw.connectors.everymatrix.data.Response;
import com.dashur.integration.extw.connectors.everymatrix.data.WinRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.WinResponse;
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

@Named("everymatrix-connector")
@Singleton
@Slf4j
public class EveryMatrixConnectorServiceImpl implements ConnectorService {
  @Inject ExtwIntegConfiguration config;
  @Inject @RestClient EveryMatrixClientService clientService;

  private EveryMatrixConfiguration emConfig;

  @PostConstruct
  public void init() {
    emConfig = config.configuration(Constant.OPERATOR_EVERYMATRIX, EveryMatrixConfiguration.class);
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

    if (!emConfig.getCompanySettings().containsKey(companyId)) {
      throw new ApplicationException("company [%s] config is not exists", companyId);
    }

    String hmacKey = emConfig.getCompanySettings().get(companyId).getHmacKey();
    String computedHmacHash = HmacUtil.hash(hmacKey, rawData);

    if (!computedHmacHash.equals(hmacHash)) {
      log.warn(
          "hmac-hash is not same with computed-hmac-hash [{} vs {}]", hmacHash, computedHmacHash);
      //      throw new ApplicationException(
      //          "hmac-hash is not same with computed-hmac-hash [%s vs %s]", hmacHash,
      // computedHmacHash);
    }
  }

  @Override
  public DasAuthResponse auth(Long companyId, DasAuthRequest request) {
    EveryMatrixConfiguration.CompanySetting setting = emConfig.getCompanySettings().get(companyId);
    AuthenticateRequest operatorRq =
        (AuthenticateRequest) Utils.map(setting.getHashSecret(), request);
    AuthenticateResponse operatorRs = clientService.authenticate(setting.getProvider(), operatorRq);
    if (Utils.isSuccess(operatorRs)) {
      return (DasAuthResponse) Utils.map(request, operatorRs);
    }
    throw Utils.toException(operatorRs);
  }

  @Override
  public DasBalanceResponse balance(Long companyId, DasBalanceRequest request) {
    EveryMatrixConfiguration.CompanySetting setting = emConfig.getCompanySettings().get(companyId);
    BalanceRequest operatorRq = (BalanceRequest) Utils.map(setting.getHashSecret(), request);
    BalanceResponse operatorRs = clientService.balance(setting.getProvider(), operatorRq);
    if (Utils.isSuccess(operatorRs)) {
      return (DasBalanceResponse) Utils.map(request, operatorRs);
    }
    throw Utils.toException(operatorRs);
  }

  @Override
  public DasTransactionResponse transaction(Long companyId, DasTransactionRequest request) {
    EveryMatrixConfiguration.CompanySetting setting = emConfig.getCompanySettings().get(companyId);

    if (DasTransactionCategory.WAGER == request.getCategory()) {
      BetRequest operatorRq = (BetRequest) Utils.map(setting.getHashSecret(), request);
      BetResponse operatorRs = clientService.bet(setting.getProvider(), operatorRq);
      if (Utils.isSuccess(operatorRs)) {
        return (DasTransactionResponse) Utils.map(request, operatorRs);
      }
      BaseException error = Utils.toException(operatorRs);

      if (error instanceof DuplicateException) {
        BigDecimal balance = BigDecimal.ZERO;
        {
          DasBalanceRequest balRq = new DasBalanceRequest();
          balRq.setToken(request.getToken());
          balRq.setReqId(request.getReqId());
          balRq.setTimestamp(request.getTimestamp());
          balRq.setCurrency(request.getCurrency());
          balRq.setAccountExtRef(request.getAccountExtRef());

          DasBalanceResponse balRs = balance(companyId, balRq);
          balance = balRs.getBalance();
        }

        DasTransactionResponse hasErrorButCanResponse = new DasTransactionResponse();

        hasErrorButCanResponse.setToken(request.getToken());
        hasErrorButCanResponse.setBalance(balance);
        hasErrorButCanResponse.setExtTxId(String.valueOf(request.getTxId()));
        hasErrorButCanResponse.setTimestamp(new Date());
        hasErrorButCanResponse.setReqId(request.getReqId());

        return hasErrorButCanResponse;
      }

      throw error;
    } else if (DasTransactionCategory.PAYOUT == request.getCategory()) {
      WinRequest operatorRq = (WinRequest) Utils.map(setting.getHashSecret(), request);
      WinResponse operatorRs = clientService.win(setting.getProvider(), operatorRq);
      if (Utils.isSuccess(operatorRs)) {
        return (DasTransactionResponse) Utils.map(request, operatorRs);
      }
      BaseException error = Utils.toException(operatorRs);

      if (error instanceof DuplicateException) {
        BigDecimal balance = BigDecimal.ZERO;
        {
          DasBalanceRequest balRq = new DasBalanceRequest();
          balRq.setToken(request.getToken());
          balRq.setReqId(request.getReqId());
          balRq.setTimestamp(request.getTimestamp());
          balRq.setCurrency(request.getCurrency());
          balRq.setAccountExtRef(request.getAccountExtRef());

          DasBalanceResponse balRs = balance(companyId, balRq);
          balance = balRs.getBalance();
        }

        DasTransactionResponse hasErrorButCanResponse = new DasTransactionResponse();

        hasErrorButCanResponse.setToken(request.getToken());
        hasErrorButCanResponse.setBalance(balance);
        hasErrorButCanResponse.setExtTxId(String.valueOf(request.getTxId()));
        hasErrorButCanResponse.setTimestamp(new Date());
        hasErrorButCanResponse.setReqId(request.getReqId());

        return hasErrorButCanResponse;
      }

      throw Utils.toException(operatorRs);
    } else if (DasTransactionCategory.REFUND == request.getCategory()) {
      CancelRequest operatorRq = (CancelRequest) Utils.map(setting.getHashSecret(), request);
      CancelResponse operatorRs = clientService.cancel(setting.getProvider(), operatorRq);
      if (Utils.isSuccess(operatorRs)) {
        return (DasTransactionResponse) Utils.map(request, operatorRs);
      }
      BaseException error = Utils.toException(operatorRs);

      if (error instanceof DuplicateException || error instanceof EntityNotExistException) {
        BigDecimal balance = BigDecimal.ZERO;
        {
          DasBalanceRequest balRq = new DasBalanceRequest();
          balRq.setToken(request.getToken());
          balRq.setReqId(request.getReqId());
          balRq.setTimestamp(request.getTimestamp());
          balRq.setCurrency(request.getCurrency());
          balRq.setAccountExtRef(request.getAccountExtRef());

          DasBalanceResponse balRs = balance(companyId, balRq);
          balance = balRs.getBalance();
        }

        DasTransactionResponse hasErrorButCanResponse = new DasTransactionResponse();

        hasErrorButCanResponse.setToken(request.getToken());
        hasErrorButCanResponse.setBalance(balance);
        hasErrorButCanResponse.setExtTxId(String.valueOf(request.getTxId()));
        hasErrorButCanResponse.setTimestamp(new Date());
        hasErrorButCanResponse.setReqId(request.getReqId());

        return hasErrorButCanResponse;
      }

      throw Utils.toException(operatorRs);
    } else {
      throw new ApplicationException(
          "Unable to find handler for the tx - category => %s", request.getCategory());
    }
  }

  @Override
  public DasEndRoundResponse endRound(Long companyId, DasEndRoundRequest request) {
    EveryMatrixConfiguration.CompanySetting setting = emConfig.getCompanySettings().get(companyId);
    WinRequest operatorRq = (WinRequest) Utils.map(setting.getHashSecret(), request);
    WinResponse operatorRs = clientService.win(setting.getProvider(), operatorRq);
    if (Utils.isSuccess(operatorRs)) {
      DasEndRoundResponse endRs = new DasEndRoundResponse();
      endRs.setToken(request.getToken());
      endRs.setBalance(operatorRs.getTotalBalance());
      endRs.setTimestamp(new Date());
      endRs.setReqId(request.getReqId());
      return endRs;
    }
    BaseException error = Utils.toException(operatorRs);
    if (error instanceof DuplicateException || error instanceof EntityNotExistException) {
      BigDecimal balance = BigDecimal.ZERO;
      {
        DasBalanceRequest balRq = new DasBalanceRequest();
        balRq.setToken(request.getToken());
        balRq.setReqId(request.getReqId());
        balRq.setTimestamp(request.getTimestamp());
        balRq.setCurrency(request.getCurrency());
        balRq.setAccountExtRef(request.getAccountExtRef());

        DasBalanceResponse balRs = balance(companyId, balRq);
        balance = balRs.getBalance();
      }

      DasEndRoundResponse hasErrorButCanResponse = new DasEndRoundResponse();

      hasErrorButCanResponse.setToken(request.getToken());
      hasErrorButCanResponse.setBalance(balance);
      hasErrorButCanResponse.setTimestamp(new Date());
      hasErrorButCanResponse.setReqId(request.getReqId());

      return hasErrorButCanResponse;
    }

    throw Utils.toException(operatorRs);
  }

  @Override
  public void validateIp(Long companyId, String callerIp) {
    if (emConfig.isValidateIps()) {
      if (CommonUtils.isEmptyOrNull(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP Validation is enabled, but caller ip is empty [%s]",
            callerIp);
      }

      callerIp = callerIp.trim();

      if (!emConfig.getWhitelistIps().contains(callerIp)) {
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
    static Boolean isSuccess(Response response) {
      return "Ok".equals(response.getStatus());
    }

    static BaseException toException(Response response) {
      if (isSuccess(response)) {
        log.error(
            "try to mapped response to exception, but it should be a successful response: [%s]",
            response);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (Objects.isNull(response.getErrorCode())) {
        log.error("try to mapped response to exception, error-code is empty: [%s]", response);
        return new ApplicationException("Un-handle response mapping error.");
      }

      if (101 == response.getErrorCode().intValue()) {
        return new ApplicationException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (102 == response.getErrorCode().intValue()) {
        return new AuthException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (103 == response.getErrorCode().intValue()) {
        return new AuthException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (105 == response.getErrorCode().intValue()) {
        return new PaymentException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (106 == response.getErrorCode().intValue()) {
        return new AuthException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (107 == response.getErrorCode().intValue()) {
        return new AuthException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (108 == response.getErrorCode().intValue()) {
        return new ApplicationException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (109 == response.getErrorCode().intValue()) {
        return new EntityNotExistException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (110 == response.getErrorCode().intValue()) {
        return new DuplicateException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (111 == response.getErrorCode().intValue()) {
        return new AuthException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (112 == response.getErrorCode().intValue()) {
        return new PaymentException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (114 == response.getErrorCode().intValue()) {
        return new ApplicationException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      if (115 == response.getErrorCode().intValue()) {
        return new ApplicationException(
            "Connector response [%s] - [%s] - [%s]",
            response.getErrorCode(), response.getErrorDescription(), response.getLogId());
      }

      return new ApplicationException(
          "Connector response [%s] - [%s] - [%s]",
          response.getErrorCode(), response.getErrorDescription(), response.getLogId());
    }

    /**
     * Map Dashur request object into EveryMatrix request Objec.t
     *
     * @param input
     * @return
     */
    static Request map(String hashSecret, DasRequest input) {
      Request output;
      if (input instanceof DasAuthRequest) {
        AuthenticateRequest output2 = new AuthenticateRequest();

        output2.setLaunchToken(input.getToken());
        output2.setRequestScope("country,age");

        output = output2;
      } else if (input instanceof DasBalanceRequest) {
        BalanceRequest output2 = new BalanceRequest();
        DasBalanceRequest input2 = (DasBalanceRequest) input;

        output2.setToken(input.getToken());
        output2.setCurrency(input2.getCurrency());

        output = output2;
      } else if (input instanceof DasTransactionRequest) {
        if (DasTransactionCategory.WAGER == ((DasTransactionRequest) input).getCategory()) {
          BetRequest output2 = new BetRequest();
          DasTransactionRequest input2 = (DasTransactionRequest) input;

          output2.setToken(input2.getToken());
          output2.setCurrency(input2.getCurrency());
          output2.setAmount(input2.getAmount());
          output2.setExternalId(String.valueOf(input2.getTxId()));
          output2.setGameId(String.valueOf(input2.getItemId()));
          output2.setRoundId(input2.getRoundId());

          output = output2;
        } else if (DasTransactionCategory.PAYOUT == ((DasTransactionRequest) input).getCategory()) {
          WinRequest output2 = new WinRequest();
          DasTransactionRequest input2 = (DasTransactionRequest) input;

          output2.setToken(input2.getToken());
          output2.setCurrency(input2.getCurrency());
          output2.setAmount(input2.getAmount());
          output2.setExternalId(String.valueOf(input2.getTxId()));
          output2.setGameId(String.valueOf(input2.getItemId()));
          output2.setRoundId(input2.getRoundId());
          output2.setBetExternalId(
              input2.getRoundId()); // roundId is the first wager tx for the round.
          output2.setRoundEnd(Boolean.FALSE);
          output2.setBonusId(
              ""); // TODO: Fix this, currently the bonus id is not forwarded to ext-w client

          output = output2;
        } else if (DasTransactionCategory.REFUND == ((DasTransactionRequest) input).getCategory()) {
          CancelRequest output2 = new CancelRequest();
          DasTransactionRequest input2 = (DasTransactionRequest) input;

          output2.setToken(input2.getToken());
          output2.setExternalId(String.valueOf(input2.getTxId()));
          output2.setCanceledExternalId(String.valueOf(input2.getRefundTxId()));

          output = output2;
        } else {
          throw new ApplicationException("Unknown input, not mapped [%s] - category", input);
        }
      } else if (input instanceof DasEndRoundRequest) {
        WinRequest output2 = new WinRequest();
        DasEndRoundRequest input2 = (DasEndRoundRequest) input;

        output2.setToken(input2.getToken());
        output2.setCurrency(input2.getCurrency());
        output2.setAmount(BigDecimal.ZERO);
        // append -end behind to ensure its unique, as the round id is usually the first wager id
        output2.setExternalId(input2.getTxId() + "-end");
        output2.setGameId(String.valueOf(input2.getItemId()));
        output2.setRoundId(input2.getRoundId());
        output2.setBetExternalId(
            String.valueOf(input2.getRoundId())); // round id is the original wager tx.
        output2.setRoundEnd(Boolean.TRUE);

        output = output2;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", input);
      }

      // set the hash.
      output.setHash(hash(hashSecret, output));
      return output;
    }

    static DasResponse map(DasRequest request, Response input) {
      DasResponse output;

      if (input instanceof AuthenticateResponse) {
        DasAuthResponse output2 = new DasAuthResponse();
        DasAuthRequest request2 = (DasAuthRequest) request;
        AuthenticateResponse input2 = (AuthenticateResponse) input;

        output2.setToken(input2.getToken());
        output2.setAccountExtRef(input2.getUserId());
        output2.setBalance(new DasMoney(input2.getCurrency(), input2.getTotalBalance()));
        output2.setCountry(input2.getCountry());
        if (CommonUtils.isEmptyOrNull(input2.getUsername())) {
          output2.setUsername("ref-" + input2.getUserId());
        } else {
          output2.setUsername(input2.getUsername());
        }
        output2.setCurrency(input2.getCurrency());
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (input instanceof BalanceResponse) {
        DasBalanceResponse output2 = new DasBalanceResponse();
        DasBalanceRequest request2 = (DasBalanceRequest) request;
        BalanceResponse input2 = (BalanceResponse) input;

        output2.setToken(request2.getToken());
        output2.setBalance(input2.getTotalBalance());
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (input instanceof BetResponse) {
        DasTransactionResponse output2 = new DasTransactionResponse();
        DasTransactionRequest request2 = (DasTransactionRequest) request;
        BetResponse input2 = (BetResponse) input;

        output2.setToken(request2.getToken());
        output2.setBalance(input2.getTotalBalance());
        output2.setExtTxId(String.valueOf(request2.getTxId()));
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (input instanceof WinResponse) {
        DasTransactionResponse output2 = new DasTransactionResponse();
        DasTransactionRequest request2 = (DasTransactionRequest) request;
        WinResponse input2 = (WinResponse) input;

        output2.setToken(request2.getToken());
        output2.setBalance(input2.getTotalBalance());
        output2.setExtTxId(String.valueOf(request2.getTxId()));
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else if (input instanceof CancelResponse) {
        DasTransactionResponse output2 = new DasTransactionResponse();
        DasTransactionRequest request2 = (DasTransactionRequest) request;
        CancelResponse input2 = (CancelResponse) input;

        output2.setToken(request2.getToken());
        output2.setBalance(input2.getTotalBalance());
        output2.setExtTxId(String.valueOf(request2.getTxId()));
        output2.setTimestamp(new Date());
        output2.setReqId(request2.getReqId());

        output = output2;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", input);
      }

      return output;
    }

    /**
     * @param hashSecret
     * @param request
     */
    static String hash(String hashSecret, Request request) {
      String methodName = "";
      String date = HASH_TIME_FORMAT.format(new Date());

      if (request instanceof AuthenticateRequest) {
        methodName = "Authenticate";
      } else if (request instanceof BalanceRequest) {
        methodName = "GetBalance";
      } else if (request instanceof BetRequest) {
        methodName = "Bet";
      } else if (request instanceof WinRequest) {
        methodName = "Win";
      } else if (request instanceof CancelRequest) {
        methodName = "Cancel";
      } else {
        throw new ApplicationException("Unable to find method name. request => [%s]", request);
      }

      return DigestUtils.md5Hex(String.format("%s%s%s", methodName, date, hashSecret));
    }
  }
}
