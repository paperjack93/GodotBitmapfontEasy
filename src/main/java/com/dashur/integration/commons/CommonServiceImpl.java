package com.dashur.integration.commons;

import com.dashur.integration.commons.VendorConfig.VendorInfo;
import com.dashur.integration.commons.auth.HashToken;
import com.dashur.integration.commons.auth.RefreshToken;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.cache.CacheService;
import com.dashur.integration.commons.data.AuthResponse;
import com.dashur.integration.commons.data.BalanceResponse;
import com.dashur.integration.commons.data.SubTxCategory;
import com.dashur.integration.commons.data.TransactionFeedResponse;
import com.dashur.integration.commons.data.TransactionResponse;
import com.dashur.integration.commons.data.TransactionRoundResponse;
import com.dashur.integration.commons.data.TransactionRoundStatus;
import com.dashur.integration.commons.data.TxCategory;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.domain.model.AccountBalance;
import com.dashur.integration.commons.domain.model.Transaction;
import com.dashur.integration.commons.domain.model.TransactionCreateRequest;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.rest.model.CampaignMemberModelExt;
import com.dashur.integration.commons.rest.model.CampaignModelExt;
import com.dashur.integration.commons.rest.model.CampaignVoucherModel;
import com.dashur.integration.commons.rest.model.CampaignVoucherModelExt;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import com.dashur.integration.commons.rest.model.TransactionModel;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import com.dashur.integration.commons.utils.CommonUtils;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.http.HttpStatus;

@ApplicationScoped
@Slf4j
public class CommonServiceImpl implements CommonService {

  @Inject CacheService cacheService;

  @Inject DomainService domainService;

  @Override
  public RequestContext context(
      VendorInfo vendor, String reqToken, Locale locale, String platform) {
    RequestContext ctx = RequestContext.instance();
    HashToken hashToken = null;

    // Parse token and get platform if it is null because not all systems send platform each time
    if (HashToken.isHashToken(reqToken)) {
      hashToken = HashToken.parse(reqToken);
      if (platform == null) platform = hashToken.getPlatform();
    }

    if (vendor == null) throw new IllegalArgumentException("Vendor config is null or empty");

    String appId = vendor.platformInfo(platform).getAppId();
    String appCredentials = vendor.platformInfo(platform).getAppCredential();

    if (CommonUtils.isWhitespaceOrNull(reqToken))
      throw new IllegalArgumentException("Request token is null or empty");
    if (CommonUtils.isWhitespaceOrNull(appId))
      throw new IllegalArgumentException("appId is null or empty");
    if (CommonUtils.isWhitespaceOrNull(appCredentials))
      throw new IllegalArgumentException("appCredentials is null or empty");

    if (locale == null) locale = Locale.ENGLISH;

    String cacheKey;
    String accessToken;
    String refreshToken;
    Token token;

    if (hashToken == null) {
      accessToken = null;
      refreshToken = reqToken;
      hashToken = HashToken.fromRefreshToken(platform, locale, refreshToken);
    } else {
      cacheKey = String.format("%s-%s", vendor.getVendorId(), hashToken.getUserId());
      accessToken = cacheService.getAccessToken(cacheKey);
      refreshToken = cacheService.getRefreshToken(hashToken.getUserId().toString());

      if (!CommonUtils.isTokenExpired(accessToken)) {
        // Commented to reduce logging
        //        if (config.isTestCompany(hashToken.getUsername())) {
        //          log.info(
        //              "[{}], seq:{}, access/refresh token from cache: {} / {}",
        //              hashToken.getUsername(),
        //              ctx.getUuid(),
        //              accessToken,
        //              refreshToken);
        //        }
      } else {
        // In case accessToken is expired, will need to refresh
        accessToken = null;
      }
    }

    if (accessToken == null) {
      if (refreshToken == null) {
        throw new AuthException("Session token expired");
      }

      cacheKey = String.format("%s-%s", vendor.getVendorId(), hashToken.getUserId());
      token = domainService.refreshToken(ctx, refreshToken, appId, appCredentials);
      cacheService.putAccessToken(cacheKey, token.getAccessToken());
      cacheService.putRefreshToken(hashToken.getUserId().toString(), token.getRefreshToken());

      // Redefine hashToken as our internal tokens have changes
      hashToken = HashToken.fromRefreshToken(platform, locale, refreshToken);

      // Commented to reduce logging
      //      if (config.isTestCompany(hashToken.getUsername())) {
      //        log.info(
      //            "[{}], seq:{}, access/refresh token from dashur: {} / {}",
      //            hashToken.getUsername(),
      //            ctx.getUuid(),
      //            token.getAccessToken(),
      //            token.getRefreshToken());
      //      }
    } else {
      token = new Token(accessToken, refreshToken);
    }

    RefreshToken parsedRefreshToken = RefreshToken.parse(token.getRefreshToken());
    long linkedTenantId = parsedRefreshToken.getLinkedTenantId();

    ctx =
        ctx.withAccessToken(token.getAccessToken())
            .withHashToken(hashToken)
            .withUsername(hashToken.getUsername())
            .withAccountId(hashToken.getAccountId())
            .withUserId(hashToken.getUserId())
            .withPlatform(hashToken.getPlatform())
            .withLanguage(locale.toString())
            .withAccountPath(parsedRefreshToken.getAccountPath())
            .withApplicationId(parsedRefreshToken.getApplicationId())
            .withTenantId(parsedRefreshToken.getTenantId())
            .withLinkedTenantId(linkedTenantId == 0 ? null : linkedTenantId);

    String currency = domainService.getWalletCurrency(ctx);
    return ctx.withCurrency(currency);
  }

  @Override
  public RequestContext context(VendorInfo vendor, String userId, String platform) {
    RequestContext ctx = RequestContext.instance();
    VendorConfig.PlatformInfo platformInfo = vendor.platformInfo(platform);
    String cacheKey = String.format("%s-%s", vendor.getVendorId(), userId);
    String accessToken = cacheService.getAccessToken(cacheKey);
    String refreshToken = cacheService.getRefreshToken(userId);
    Token token;

    if (!CommonUtils.isTokenExpired(accessToken)) {
      token = new Token(accessToken, refreshToken);
    } else {
      accessToken = cacheService.getAccessToken(vendor.getApiClientId());

      if (CommonUtils.isEmptyOrNull(accessToken)) {
        // do a login using api client to refresh expired member token
        token =
            domainService.loginAppClient(
                ctx, vendor.getApiClientId(), vendor.getApiClientCredential());
        accessToken = token.getAccessToken();
        cacheService.putAccessToken(vendor.getApiClientId(), accessToken);
      }

      if (CommonUtils.isEmptyOrNull(refreshToken) || CommonUtils.isTokenExpired(refreshToken)) {
        ctx = ctx.withAccessToken(accessToken);
        token = domainService.loginAsMember(ctx, Long.parseLong(userId));
        refreshToken = token.getRefreshToken();
        cacheService.putRefreshToken(userId, refreshToken);
      }

      token =
          domainService.refreshToken(
              ctx, refreshToken, platformInfo.getAppId(), platformInfo.getAppCredential());
      cacheService.putAccessToken(cacheKey, token.getAccessToken());
      cacheService.putRefreshToken(userId, token.getRefreshToken());
    }

    Locale locale = Locale.ENGLISH;
    HashToken hashToken =
        HashToken.fromRefreshToken(platformInfo.getCode(), locale, token.getRefreshToken());
    RefreshToken parsedRefreshToken = RefreshToken.parse(token.getRefreshToken());
    long linkedTenantId = parsedRefreshToken.getLinkedTenantId();

    ctx =
        ctx.withAccessToken(token.getAccessToken())
            .withHashToken(hashToken)
            .withUsername(hashToken.getUsername())
            .withAccountId(hashToken.getAccountId())
            .withUserId(hashToken.getUserId())
            .withPlatform(hashToken.getPlatform())
            .withLanguage(locale.toString())
            .withAccountPath(parsedRefreshToken.getAccountPath())
            .withApplicationId(parsedRefreshToken.getApplicationId())
            .withTenantId(parsedRefreshToken.getTenantId())
            .withLinkedTenantId(linkedTenantId == 0 ? null : linkedTenantId);

    String currency = domainService.getWalletCurrency(ctx);
    return ctx.withCurrency(currency);
  }

  @Override
  public RequestContext context(String clientId, String clientPassword) {
    RequestContext ctx = RequestContext.instance();
    String accessToken = cacheService.getAccessToken(clientId);

    if (CommonUtils.isEmptyOrNull(accessToken)) {
      Token token = domainService.loginAppClient(ctx, clientId, clientPassword);
      accessToken = token.getAccessToken();
      cacheService.putAccessToken(clientId, accessToken);
    }

    return ctx.withAccessToken(accessToken);
  }

  @Override
  public void authenticateVendor(VendorInfo vendor, String callerIp) {
    if (vendor.isValidateIps()) {
      if (CommonUtils.isWhitespaceOrNull(callerIp)) {
        throw new AuthException("callerIp is null or empty");
      }

      List<String> cidrList = vendor.getCIDRList();
      for (String cidr : cidrList) {
        SubnetUtils utils = new SubnetUtils(cidr);
        boolean inRange = utils.getInfo().isInRange(callerIp);
        if (inRange) {
          return;
        }
      }

      if (!vendor.getWhitelistIps().contains(callerIp)) {
        throw new AuthException("[%s] is not whitelisted to access", callerIp);
      }
    }
  }

  @Override
  public void authenticateVendor(VendorInfo vendor, String auth, String callerIp) {
    authenticateVendor(vendor, callerIp);

    if (CommonUtils.isWhitespaceOrNull(auth)) {
      throw new AuthException("auth is null or empty");
    }

    String[] tokens = auth.split(" ");

    if (tokens.length != 2) {
      throw new AuthException("auth token is not valid");
    }

    if (!"basic".equalsIgnoreCase(tokens[0])) {
      throw new AuthException("auth token format is not valid");
    }

    if (!tokens[1].equals(vendor.getAuth())) {
      throw new AuthException("access denied!");
    }
  }

  @Override
  public AuthResponse authenticate(RequestContext ctx) {
    AccountBalance balance = domainService.getAccountBalance(ctx);

    AuthResponse response = new AuthResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(balance.getCurrency());
    response.setBalance(balance.getBalance());
    response.setCountryCode("CN"); // To be overridden at controller if necessary
    return response;
  }

  @Override
  public BalanceResponse balance(RequestContext ctx) {
    AccountBalance balance = domainService.getAccountBalance(ctx);

    BalanceResponse response = new BalanceResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(balance.getCurrency());
    response.setBalance(balance.getBalance());
    return response;
  }

  @Override
  public TransactionResponse wager(
      RequestContext ctx,
      String txExtRef,
      String txExtRoundRef,
      String gameRef,
      BigDecimal amount,
      BigDecimal bonusAmount,
      BigDecimal poolAmount,
      SubTxCategory subCategory,
      Map<String, Object> metadata) {

    TransactionCreateRequest txRq =
        new TransactionCreateRequest(
            ctx.getAccountId(),
            TxCategory.WAGER.getCode(),
            subCategory.getCode(),
            new TransactionCreateRequest.Money(ctx.getCurrency(), amount),
            txExtRef,
            gameRef,
            new TransactionCreateRequest.Metadata(txExtRoundRef, metadata));

    List<Transaction> results = domainService.createTransaction(ctx, Lists.newArrayList(txRq));

    //    REFACTOR this to DomainService.
    //    for (Transaction tx : results) {
    //      if ("REFUND".equals(tx.getCategory())) {
    //        throw new ApplicationException("Refund exist for wagering transaction");
    //      }
    //    }

    Transaction transaction = results.get(0);

    TransactionResponse response = new TransactionResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(transaction.getCurrencyUnit());
    response.setAmount(transaction.getAmount());
    response.setBalance(transaction.getBalance());
    response.setTxId(transaction.getId().toString());
    response.setCategory(transaction.getCategory());
    response.setSubCategory(transaction.getSubCategory());

    if (Objects.nonNull(transaction.getMetaData())
        && transaction.getMetaData().containsKey(Constant.DAS_TX_META_DATA_DUPLICATE)) {
      Object status = transaction.getMetaData().containsKey(Constant.DAS_TX_META_DATA_DUPLICATE);
      response.setDuplicate(Boolean.valueOf(status.toString()));
    }

    return response;
  }

  @Override
  public TransactionResponse payout(
      RequestContext ctx,
      String txExtRef,
      String txExtRoundRef,
      String gameRef,
      BigDecimal amount,
      BigDecimal bonusAmount,
      BigDecimal poolAmount,
      SubTxCategory subCategory,
      Map<String, Object> metadata) {

    TransactionCreateRequest txRq =
        new TransactionCreateRequest(
            ctx.getAccountId(),
            TxCategory.PAYOUT.getCode(),
            subCategory.getCode(),
            new TransactionCreateRequest.Money(ctx.getCurrency(), amount),
            txExtRef,
            gameRef,
            new TransactionCreateRequest.Metadata(txExtRoundRef, metadata));

    Transaction transaction = domainService.createTransaction(ctx, Lists.newArrayList(txRq)).get(0);

    TransactionResponse response = new TransactionResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(transaction.getCurrencyUnit());
    response.setAmount(transaction.getAmount());
    response.setBalance(transaction.getBalance());
    response.setTxId(transaction.getId().toString());
    response.setCategory(transaction.getCategory());
    response.setSubCategory(transaction.getSubCategory());

    if (Objects.nonNull(transaction.getMetaData())
        && transaction.getMetaData().containsKey(Constant.DAS_TX_META_DATA_DUPLICATE)) {
      Object status = transaction.getMetaData().containsKey(Constant.DAS_TX_META_DATA_DUPLICATE);
      response.setDuplicate(Boolean.valueOf(status.toString()));
    }

    return response;
  }

  @Override
  public TransactionResponse endround(
      RequestContext ctx,
      String txExtRef,
      String txExtRoundRef,
      String gameRef,
      Optional<Map<String, Object>> optionalMeta) {

    Map<String, Object> metadata = optionalMeta.orElse(new HashMap<>());

    TransactionCreateRequest txRq =
        new TransactionCreateRequest(
            ctx.getAccountId(),
            TxCategory.ENDROUND.getCode(),
            "",
            new TransactionCreateRequest.Money(ctx.getCurrency(), BigDecimal.ZERO),
            txExtRef,
            gameRef,
            new TransactionCreateRequest.Metadata(txExtRoundRef, metadata));

    Transaction transaction = domainService.createTransaction(ctx, Lists.newArrayList(txRq)).get(0);

    TransactionResponse response = new TransactionResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(transaction.getCurrencyUnit());
    response.setAmount(transaction.getAmount());
    response.setBalance(transaction.getBalance());
    response.setTxId(transaction.getId().toString());
    response.setCategory(transaction.getCategory());
    response.setSubCategory(transaction.getSubCategory());
    return response;
  }

  @Override
  public TransactionResponse refund(RequestContext ctx, String originTxExtRef) {
    Transaction transaction = domainService.refundTransaction(ctx, originTxExtRef);

    TransactionResponse response = new TransactionResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(transaction.getCurrencyUnit());
    response.setAmount(transaction.getAmount());
    response.setBalance(transaction.getBalance());
    response.setTxId(transaction.getId().toString());
    response.setCategory(transaction.getCategory());
    response.setSubCategory(transaction.getSubCategory());
    return response;
  }

  @Override
  public TransactionResponse refund(
      RequestContext ctx,
      String originTxExtRef,
      String txExtRoundRef,
      String gameRef,
      BigDecimal amount) {
    Map<String, Object> metadata = new HashMap<>();

    TransactionCreateRequest txRq =
        new TransactionCreateRequest(
            ctx.getAccountId(),
            TxCategory.REFUND.getCode(),
            "",
            new TransactionCreateRequest.Money(ctx.getCurrency(), amount),
            originTxExtRef,
            gameRef,
            new TransactionCreateRequest.Metadata(txExtRoundRef, metadata));

    Transaction transaction = domainService.createTransaction(ctx, Lists.newArrayList(txRq)).get(0);

    TransactionResponse response = new TransactionResponse();
    response.setToken(ctx.getHashToken().toString());
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(transaction.getCurrencyUnit());
    response.setAmount(transaction.getAmount());
    response.setBalance(transaction.getBalance());
    response.setTxId(transaction.getId().toString());
    response.setCategory(transaction.getCategory());
    response.setSubCategory(transaction.getSubCategory());
    return response;
  }

  @Override
  public TransactionResponse getTransaction(RequestContext ctx, String txExtRef) {
    TransactionModel tx = domainService.findTransaction(ctx, ctx.getAccountId(), txExtRef);

    if (tx.getDatas().isEmpty()) {
      throw new EntityNotExistException(HttpStatus.SC_NOT_FOUND, "Tx not found for given ext_ref");
    }

    TransactionModel.DataModel txDataModel = tx.getDatas().get(0);

    TransactionResponse response = new TransactionResponse();
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(txDataModel.getCurrencyUnit());
    response.setAmount(txDataModel.getAmount());
    response.setBalance(txDataModel.getBalance());
    response.setTxId(txDataModel.getId().toString());
    response.setCategory(txDataModel.getCategory());
    response.setSubCategory(txDataModel.getSubCategory());
    return response;
  }

  @Override
  public TransactionFeedResponse getTransactionFeed(RequestContext ctx, Long txId) {
    TransactionFeedModel tx = domainService.findTransactionFeedById(ctx, txId);

    TransactionFeedResponse response = new TransactionFeedResponse();
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(tx.getCurrencyUnit());
    response.setAmount(tx.getAmount());
    response.setBalance(tx.getBalance());
    response.setTxId(tx.getId().toString());
    response.setExtRef(tx.getExternalRef());
    response.setCategory(tx.getCategory());
    response.setSubCategory(tx.getSubCategory());
    response.setNumOfWager(tx.getNumOfWager());
    response.setNumOfPayout(tx.getNumOfPayout());
    response.setNumOfRefund(tx.getNumOfRefund());
    response.setMetaData(tx.getMetaData());
    return response;
  }

  @Override
  public TransactionFeedResponse getTransactionFeed(
      RequestContext ctx, String txExtRef, TxCategory category) {
    TransactionFeedModel tx = domainService.findTransactionFeedByExtRef(ctx, txExtRef, category);

    TransactionFeedResponse response = new TransactionFeedResponse();
    response.setAccountId(String.valueOf(ctx.getAccountId()));
    response.setUserId(String.valueOf(ctx.getUserId()));
    response.setCurrency(tx.getCurrencyUnit());
    response.setAmount(tx.getAmount());
    response.setBalance(tx.getBalance());
    response.setTxId(tx.getId().toString());
    response.setExtRef(tx.getExternalRef());
    response.setCategory(tx.getCategory());
    response.setSubCategory(tx.getSubCategory());
    response.setNumOfWager(tx.getNumOfWager());
    response.setNumOfPayout(tx.getNumOfPayout());
    response.setNumOfRefund(tx.getNumOfRefund());
    response.setMetaData(tx.getMetaData());
    return response;
  }

  @Override
  public TransactionRoundResponse getTransactionFeedRound(
      RequestContext ctx, String txExtRoundRef) {
    TransactionRoundModel txRound =
        domainService.findTransactionRoundByRoundExtRef(ctx, txExtRoundRef);

    TransactionRoundResponse response = new TransactionRoundResponse();
    response.setAccountId(txRound.getAccountId());
    response.setCurrency(txRound.getCurrencyUnit());
    response.setRoundId(txRound.getExternalRef());
    response.setStatus(TransactionRoundStatus.valueOf(txRound.getStatus()));
    response.setTransactionIds(txRound.getTransactionIds());
    response.setNumOfWager(txRound.getNumOfWager());
    response.setNumOfPayout(txRound.getNumOfPayout());
    response.setNumOfRefund(txRound.getNumOfRefund());
    response.setSumOfWager(txRound.getSumOfWager());
    response.setSumOfPayout(txRound.getSumOfPayout());
    response.setSumOfRefundCredit(txRound.getSumOfRefundCredit());
    response.setSumOfRefundDebit(txRound.getSumOfRefundDebit());
    response.setStartBalance(txRound.getStartBalance());
    response.setLastBalance(txRound.getLastBalance());
    response.setCloseBalance(txRound.getCloseBalance());
    return response;
  }

  @Override
  public TransactionRoundResponse getTransactionRound(RequestContext ctx, String txExtRoundRef) {
    TransactionRoundModel txRound =
        domainService.findTransactionRound(ctx, ctx.getAccountId(), txExtRoundRef);

    TransactionRoundResponse response = new TransactionRoundResponse();
    response.setAccountId(txRound.getAccountId());
    response.setCurrency(txRound.getCurrencyUnit());
    response.setRoundId(txRound.getExternalRef());
    response.setStatus(TransactionRoundStatus.valueOf(txRound.getStatus()));
    response.setTransactionIds(txRound.getTransactionIds());
    response.setNumOfWager(txRound.getNumOfWager());
    response.setNumOfPayout(txRound.getNumOfPayout());
    response.setNumOfRefund(txRound.getNumOfRefund());
    response.setSumOfWager(txRound.getSumOfWager());
    response.setSumOfPayout(txRound.getSumOfPayout());
    response.setSumOfRefundCredit(txRound.getSumOfRefundCredit());
    response.setSumOfRefundDebit(txRound.getSumOfRefundDebit());
    response.setStartBalance(txRound.getStartBalance());
    response.setLastBalance(txRound.getLastBalance());
    response.setCloseBalance(txRound.getCloseBalance());
    return response;
  }

  @Override
  public List<TransactionFeedResponse> getTransactionFeedByRoundId(
      RequestContext ctx,
      Long applicationId,
      String game,
      Long roundId,
      Long dataTimeLimitInSeconds) {
    List<TransactionFeedModel> domainResult =
        domainService.findTransactionFeedByRoundId(
            ctx, applicationId, ctx.getAccountId(), game, roundId, dataTimeLimitInSeconds);
    List<TransactionFeedResponse> result = Lists.newArrayList();

    for (TransactionFeedModel tx : domainResult) {
      TransactionFeedResponse response = new TransactionFeedResponse();
      response.setAccountId(String.valueOf(ctx.getAccountId()));
      response.setUserId(String.valueOf(ctx.getUserId()));
      response.setCurrency(tx.getCurrencyUnit());
      response.setAmount(tx.getAmount());
      response.setBalance(tx.getBalance());
      response.setTxId(tx.getId().toString());
      response.setExtRef(tx.getExternalRef());
      response.setCategory(tx.getCategory());
      response.setSubCategory(tx.getSubCategory());
      response.setNumOfWager(tx.getNumOfWager());
      response.setNumOfPayout(tx.getNumOfPayout());
      response.setNumOfRefund(tx.getNumOfRefund());
      response.setMetaData(tx.getMetaData());
      result.add(response);
    }

    return result;
  }

  @Override
  public CommonUtils.Pair<List<TransactionRoundResponse>, Integer> getTransactionRound(
      RequestContext ctx,
      Long applicationId,
      String game,
      Date startTime,
      Date endTime,
      Integer pageSize,
      Integer page,
      Long dataTimeLimitInSeconds) {
    if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
      throw new ValidationException("start-time or end-time is empty or null");
    }
    if (startTime.after(endTime)) {
      throw new ValidationException(
          "should be from start-time to end-time (so start-time must be earlier end-time)");
    }
    Date now = new Date();
    if (Objects.nonNull(dataTimeLimitInSeconds) && dataTimeLimitInSeconds > 0L) {
      Long diffInSeconds = TimeUnit.SECONDS.toSeconds(now.getTime() - startTime.getTime());
      if (diffInSeconds
          > dataTimeLimitInSeconds) { // start time cannot be later than dataTimeLimitInSeconds
        throw new ValidationException("start-time cannot be later than 90 days from now");
      }
    }
    if (endTime.after(now)) {
      endTime = now; // if end-time is later than now, use end time as now.
    }
    if (Objects.isNull(page) || page.intValue() <= 0) {
      page = 1;
    }

    List<TransactionRoundResponse> results = Lists.newArrayList();
    while (results.size() < pageSize) {
      List<TransactionRoundModel> domainResults =
          domainService.findTransactionRound(
              ctx, applicationId, ctx.getAccountId(), startTime, endTime, pageSize, page);
      if (domainResults.isEmpty()) {
        break;
      }
      for (TransactionRoundModel txRound : domainResults) {
        // manual filtering of game as it wasn't available as backend filtering.
        String roundExtItemId = "";
        if (Objects.nonNull(txRound.getMetaData())) {
          roundExtItemId = txRound.getMetaData().getOrDefault("ext_item_id", "").toString();
        }
        if (roundExtItemId.equals(game)) {
          TransactionRoundResponse response = new TransactionRoundResponse();
          response.setAccountId(txRound.getAccountId());
          response.setCurrency(txRound.getCurrencyUnit());
          response.setRoundId(txRound.getExternalRef());
          response.setStatus(TransactionRoundStatus.valueOf(txRound.getStatus()));
          response.setTransactionIds(txRound.getTransactionIds());
          response.setNumOfWager(txRound.getNumOfWager());
          response.setNumOfPayout(txRound.getNumOfPayout());
          response.setNumOfRefund(txRound.getNumOfRefund());
          response.setSumOfWager(txRound.getSumOfWager());
          response.setSumOfPayout(txRound.getSumOfPayout());
          response.setSumOfRefundCredit(txRound.getSumOfRefundCredit());
          response.setSumOfRefundDebit(txRound.getSumOfRefundDebit());
          response.setStartBalance(txRound.getStartBalance());
          response.setLastBalance(txRound.getLastBalance());
          response.setCloseBalance(txRound.getCloseBalance());
          results.add(response);
        }
      }
      page++;
    }
    return new CommonUtils.Pair<>(results, page);
  }

  @Override
  public void validateCampaignAssignment(CampaignMemberModelExt modelExt) {
    CampaignModelExt campaign = modelExt.getCampaign();
    String cacheKey = String.format("%s::%s", campaign.getId(), modelExt.getAccountId());
    String cacheAssignment = cacheService.getCampaignAssignment(cacheKey);
    String newAssignment =
        String.format(
            "%s-%s-%s", campaign.getVendorId(), campaign.getGameId(), campaign.getLaunchId());

    if (!CommonUtils.isEmptyOrNull(cacheAssignment) && !cacheAssignment.equals(newAssignment)) {
      throw new ValidationException(
          "Campaign assignment '%s' already exists for '%s'", cacheKey, cacheAssignment);
    }
  }

  @Override
  public void storeCampaignAssignment(CampaignMemberModelExt modelExt) {
    CampaignModelExt campaign = modelExt.getCampaign();
    String cacheKey = String.format("%s::%s", campaign.getId(), modelExt.getAccountId());
    String cacheAssignment =
        String.format(
            "%s-%s-%s", campaign.getVendorId(), campaign.getGameId(), campaign.getLaunchId());
    cacheService.putCampaignAssignment(cacheKey, cacheAssignment);
  }

  @Override
  public void validateCampaignVoucherClaim(CampaignVoucherModelExt modelExt) {
    CampaignVoucherModel model =
        CampaignVoucherModel.newCampaignVoucherModelBuilder()
            .id(modelExt.getId())
            .campaignId(modelExt.getCampaignId())
            .campaignMemberId(modelExt.getCampaignMemberId())
            .voucherRef(modelExt.getVoucherRef())
            .build();

    String cacheKey =
        String.format("%s::%s", modelExt.getCampaignMemberId(), modelExt.getVoucherRef());
    String cacheVoucher = cacheService.getCampaignVoucherClaim(cacheKey);
    String newVoucher = CommonUtils.jsonToString(model);

    if (!CommonUtils.isEmptyOrNull(cacheVoucher) && cacheVoucher.equals(newVoucher)) {
      throw new ValidationException(
          "Campaign voucher claim '%s' already exists. '%s'", cacheKey, cacheVoucher);
    }
  }

  @Override
  public void storeCampaignVoucherClaim(CampaignVoucherModelExt modelExt) {
    CampaignVoucherModel model =
        CampaignVoucherModel.newCampaignVoucherModelBuilder()
            .id(modelExt.getId())
            .campaignId(modelExt.getCampaignId())
            .campaignMemberId(modelExt.getCampaignMemberId())
            .voucherRef(modelExt.getVoucherRef())
            .build();

    String cacheKey =
        String.format("%s::%s", modelExt.getCampaignMemberId(), modelExt.getVoucherRef());

    cacheService.putCampaignVoucherClaim(cacheKey, CommonUtils.jsonToString(model));
  }
}
