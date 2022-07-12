package com.dashur.integration.commons.domain.impl;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.auth.GrantType;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.cache.CacheService;
import com.dashur.integration.commons.data.TxCategory;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.domain.model.AccountBalance;
import com.dashur.integration.commons.domain.model.Transaction;
import com.dashur.integration.commons.domain.model.TransactionCreateRequest;
import com.dashur.integration.commons.exception.*;
import com.dashur.integration.commons.rest.AccountClientService;
import com.dashur.integration.commons.rest.ApplicationClientService;
import com.dashur.integration.commons.rest.AuthClientService;
import com.dashur.integration.commons.rest.CampaignClientService;
import com.dashur.integration.commons.rest.FeedTransactionClientService;
import com.dashur.integration.commons.rest.ItemClientService;
import com.dashur.integration.commons.rest.LauncherClientService;
import com.dashur.integration.commons.rest.TransactionClientService;
import com.dashur.integration.commons.rest.UserClientService;
import com.dashur.integration.commons.rest.WalletClientService;
import com.dashur.integration.commons.rest.model.AccountBalanceModel;
import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignMemberModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.CampaignUpdateModel;
import com.dashur.integration.commons.rest.model.ErrorModel;
import com.dashur.integration.commons.rest.model.LoginMemberModel;
import com.dashur.integration.commons.rest.model.MemberTokenModel;
import com.dashur.integration.commons.rest.model.MetaModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.SimpleApplicationItemModel;
import com.dashur.integration.commons.rest.model.SimpleItemModel;
import com.dashur.integration.commons.rest.model.SimpleUserModel;
import com.dashur.integration.commons.rest.model.SimpleWalletModel;
import com.dashur.integration.commons.rest.model.SimplifyAccountAppSettingsModel;
import com.dashur.integration.commons.rest.model.SimplifyApplicationModel;
import com.dashur.integration.commons.rest.model.SimplifyLauncherItemModel;
import com.dashur.integration.commons.rest.model.TokenModel;
import com.dashur.integration.commons.rest.model.TransactionCreateModel;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import com.dashur.integration.commons.rest.model.TransactionModel;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import com.dashur.integration.commons.utils.CommonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class DomainServiceImpl implements DomainService {
  static final Long MAX_RETRY_MILIS = 10 * 1000L; // 10 Sec

  static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Inject @RestClient AuthClientService authClientService;

  @Inject @RestClient AccountClientService accountClientService;

  @Inject @RestClient UserClientService userClientService;

  @Inject @RestClient TransactionClientService transactionClientService;

  @Inject @RestClient CampaignClientService campaignClientService;

  @Inject @RestClient WalletClientService walletClientService;

  @Inject @RestClient ApplicationClientService applicationClientService;

  @Inject @RestClient ItemClientService itemClientService;

  @Inject @RestClient LauncherClientService launcherClientService;

  @Inject @RestClient FeedTransactionClientService feedTransactionClientService;

  @Inject CacheService cacheService;

  @Inject CommonsConfig config;

  @Override
  public Token companyLogin(
      RequestContext context,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppICredential) {
    TokenModel model;
    try {
      model =
          authClientService.loginPassword(
              CommonUtils.authorizationBasic(clientId, clientCredential),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              GrantType.PASSWORD.toString(),
              companyAppId,
              companyAppICredential);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    return new Token(model.getAccessToken(), model.getRefreshToken());
  }

  @Override
  public Token refreshToken(
      @NonNull RequestContext context,
      @NonNull String refreshToken,
      @NonNull String clientId,
      @NonNull String clientCredential) {
    if (CommonUtils.isWhitespaceOrNull(refreshToken)) {
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_ARGUMENT,
          "Unable to refresh token [%s]-refresh-token is null or empty",
          refreshToken);
    }

    TokenModel model;
    try {
      model =
          authClientService.refreshToken(
              CommonUtils.authorizationBasic(clientId, clientCredential),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              GrantType.REFRESH_TOKEN.toString(),
              clientId,
              refreshToken);
    } catch (WebApplicationException e) {
      throw error(e);
    } catch (Exception ex) {
      log.error("Error calling refresh", ex);
      throw ex;
    }

    return new Token(model.getAccessToken(), model.getRefreshToken());
  }

  @Override
  public Token loginAppClient(
      @NonNull RequestContext context, @NonNull String clientId, @NonNull String clientCredential) {
    TokenModel model;
    try {
      model =
          authClientService.loginAppClient(
              CommonUtils.authorizationBasic(clientId, clientCredential),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              GrantType.CLIENT_CREDENTIALS.toString(),
              clientId,
              clientCredential);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    return new Token(model.getAccessToken(), model.getRefreshToken());
  }

  @Override
  public Token loginAsMember(@NonNull RequestContext context, @NonNull Long userId) {
    MemberTokenModel model;

    LoginMemberModel loginMemberModel = new LoginMemberModel();
    loginMemberModel.setUserId(userId);

    try {
      model =
          authClientService.loginAsMember(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              loginMemberModel);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    return new Token(model.getTokenData().getAccessToken(), model.getTokenData().getRefreshToken());
  }

  @Override
  public AccountBalance getAccountBalance(@NonNull RequestContext context) {
    AccountBalanceModel model;
    try {
      model =
          accountClientService.balance(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage());
    } catch (WebApplicationException e) {
      throw error(e);
    }

    cacheService.putCurrency(context.getAccountId(), model.getData().getCurrency());

    return new AccountBalance(
        model.getData().getCurrency(), CommonUtils.toMoney(model.getData().getBalance()));
  }

  @Override
  public String getWalletCurrency(RequestContext context) {
    String currency = cacheService.getCurrency(context.getAccountId());

    if (CommonUtils.isEmptyOrNull(currency)) {
      SimpleWalletModel wallet = getWallet(context, context.getAccountId());
      currency = wallet.getCurrencyUnit();
      cacheService.putCurrency(context.getAccountId(), currency);
    }

    return currency;
  }

  @AllArgsConstructor
  @Getter
  private static final class FilterResult {
    private List<TransactionCreateRequest> txInputs;
    private List<TransactionCreateModel> txCreates;
    private List<TransactionModel> txProcessed;
  }

  /**
   * @param txRqs
   * @return
   */
  protected FilterResult filterTransactions(@NonNull List<TransactionCreateRequest> txRqs) {
    FilterResult result = new FilterResult(txRqs, Lists.newArrayList(), Lists.newArrayList());

    for (TransactionCreateRequest txRq : txRqs) {
      result
          .getTxCreates()
          .add(
              new TransactionCreateModel(
                  txRq.getAccountId(),
                  txRq.getCategory(),
                  txRq.getSubCategory(),
                  new TransactionCreateModel.MoneyModel(
                      txRq.getAmount().getCurrency(), txRq.getAmount().getAmount()),
                  txRq.getExternalRef(),
                  txRq.getExtItemId(),
                  new TransactionCreateModel.MetadataModel(
                      txRq.getMetadata().getRoundId(), txRq.getMetadata().getVendor()),
                  txRq.getReserveId(),
                  txRq.getReserveExpiryTime()));
    }

    return result;
  }

  @Override
  public List<Transaction> createTransaction(
      @NonNull RequestContext context, @NonNull List<TransactionCreateRequest> txRqs) {
    long millis = config.getTxRetryMillis();
    List<Transaction> results = new ArrayList<>();
    FilterResult filtered = filterTransactions(txRqs);

    if (!filtered.getTxProcessed().isEmpty()
        && filtered.getTxInputs().size() != filtered.getTxProcessed().size()) {
      log.error(
          "Multi tx detected, some has been process and some has not. Will only process unprocessed tx");
    }

    TransactionModel restResult = null;

    try {
      if (!filtered.getTxCreates().isEmpty()) {
        restResult =
            transactionClientService.transaction(
                CommonUtils.authorizationBearer(context.getAccessToken()),
                context.getTimezone(),
                context.getCurrency(),
                context.getUuid().toString(),
                context.getLanguage(),
                filtered.getTxCreates().toArray(new TransactionCreateModel[] {}));
      }
    } catch (WebApplicationException e) {
      if (HttpStatus.SC_CONFLICT == e.getResponse().getStatus()) {
        while (Objects.isNull(restResult) && millis <= MAX_RETRY_MILIS) {
          MetaModel meta = null;
          List<TransactionModel.DataModel> conflicResults = Lists.newArrayList();

          try {
            for (TransactionCreateModel txCreate : filtered.getTxCreates()) {
              TransactionModel conflictResult =
                  transactionClientService.getTransaction(
                      CommonUtils.authorizationBearer(context.getAccessToken()),
                      context.getTimezone(),
                      context.getCurrency(),
                      context.getUuid().toString(),
                      context.getLanguage(),
                      context.getAccountId(),
                      txCreate.getExternalRef());

              // if it has more than 1 results, then need to check whether its a refunded tx.
              if (conflictResult.getDatas().size() > 1
                  && !"REFUND".equals(txCreate.getCategory())) {
                for (TransactionModel.DataModel txData : conflictResult.getDatas()) {
                  // if request is not refund and refund found in response then throw error
                  if ("REFUND".equals(txData.getCategory())) {
                    throw new ApplicationException("REFUND found on non-REFUND tx request");
                  }
                }
              }

              // To flag duplicated tx for identification purposes
              conflicResults =
                  conflictResult.getDatas().stream()
                      .peek(
                          data ->
                              data.getMetaData()
                                  .put(Constant.DAS_TX_META_DATA_DUPLICATE, Boolean.TRUE))
                      .collect(Collectors.toList());

              meta = conflictResult.getMeta();
            }

            restResult = new TransactionModel();
            restResult.setMeta(meta);
            restResult.setDatas(conflicResults);
          } catch (WebApplicationException e2) {
            List<String> extRefs = Lists.newArrayList();
            for (TransactionCreateModel txCreate : filtered.getTxCreates()) {
              extRefs.add(txCreate.getExternalRef());
            }
            log.error(
                "Error caught [{}] - [{}] - [{}]",
                CommonUtils.authorizationBearer(context.getAccessToken()),
                context.getAccountId(),
                extRefs.stream().map(Object::toString).collect(Collectors.joining(",")),
                e2);
            // won't try error here, will retry to find tx-es.
          }

          try {
            Thread.sleep(millis);
          } catch (InterruptedException e3) {
            log.warn("failed to sleep a thread", e3);
          }
          millis *= 2;
        }
      }

      // Either conflict not found or due to other error, throw the mapped error.
      if (Objects.isNull(restResult)
          || Objects.isNull(restResult.getDatas())
          || restResult.getDatas().isEmpty()) {
        throw error(e);
      }
    }

    List<TransactionModel.DataModel> txDataModels = Lists.newArrayList();

    if (Objects.nonNull(restResult)
        && Objects.nonNull(restResult.getDatas())
        && !restResult.getDatas().isEmpty()) {
      txDataModels.addAll(restResult.getDatas());
    }

    if (!filtered.getTxProcessed().isEmpty()) {
      for (TransactionModel processed : filtered.getTxProcessed()) {
        txDataModels.addAll(processed.getDatas());
      }
    }

    for (TransactionModel.DataModel txData : txDataModels) {
      results.add(
          new Transaction(
              txData.getId(),
              txData.getAccountId(),
              txData.getApplicationId(),
              txData.getCurrencyUnit(),
              txData.getTransactionTime(),
              txData.getWalletCode(),
              txData.getCategory(),
              txData.getSubCategory(),
              txData.getBalanceType(),
              txData.getType(),
              txData.getAmount(),
              CommonUtils.toMoney(txData.getBalance()),
              txData.getMetaData(),
              txData.getReserveId(),
              CommonUtils.toMoney(txData.getReserveBalance())));
    }

    return results;
  }

  @Override
  public Transaction refundTransaction(RequestContext context, String txExtRef) {
    TransactionModel originalTx =
        transactionClientService.getTransaction(
            CommonUtils.authorizationBearer(context.getAccessToken()),
            context.getTimezone(),
            context.getCurrency(),
            context.getUuid().toString(),
            context.getLanguage(),
            context.getAccountId(),
            txExtRef);

    if (originalTx.getDatas().isEmpty()) {
      throw new EntityNotExistException(
          HttpStatus.SC_NOT_FOUND, "Origin tx not found for given account and ext_ref");
    }

    TransactionModel.DataModel originalTxDataModel = originalTx.getDatas().get(0);
    String extItemId = String.valueOf(originalTxDataModel.getMetaData().get("ext_item_id"));
    String roundId = String.valueOf(originalTxDataModel.getMetaData().get("round_id"));
    Map<String, Object> metadata = new HashMap<>();

    TransactionCreateModel createModel =
        new TransactionCreateModel(
            context.getAccountId(),
            TxCategory.REFUND.getCode(),
            "",
            new TransactionCreateModel.MoneyModel(
                originalTxDataModel.getCurrencyUnit(), originalTxDataModel.getAmount()),
            txExtRef,
            extItemId,
            new TransactionCreateModel.MetadataModel(roundId, metadata),
            null,
            null);

    TransactionModel txModel =
        transactionClientService.transaction(
            CommonUtils.authorizationBearer(context.getAccessToken()),
            context.getTimezone(),
            context.getCurrency(),
            context.getUuid().toString(),
            context.getLanguage(),
            new TransactionCreateModel[] {createModel});

    TransactionModel.DataModel txDataModel = txModel.getDatas().get(0);
    return new Transaction(
        txDataModel.getId(),
        txDataModel.getAccountId(),
        txDataModel.getApplicationId(),
        txDataModel.getCurrencyUnit(),
        txDataModel.getTransactionTime(),
        txDataModel.getWalletCode(),
        txDataModel.getCategory(),
        txDataModel.getSubCategory(),
        txDataModel.getBalanceType(),
        txDataModel.getType(),
        txDataModel.getAmount(),
        CommonUtils.toMoney(txDataModel.getBalance()),
        txDataModel.getMetaData(),
        txDataModel.getReserveId(),
        CommonUtils.toMoney(txDataModel.getReserveBalance()));
  }

  @Override
  public CampaignModel updateCampaign(
      @NonNull RequestContext ctx, @NonNull Long campaignId, @NonNull CampaignUpdateModel model) {

    RestResponseWrapperModel<CampaignModel> result;
    try {
      result =
          campaignClientService.update(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              String.valueOf(campaignId),
              model);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException("Rest application has error");
    }

    return result.getData();
  }

  @Override
  public CampaignModel createCampaign(
      @NonNull RequestContext ctx, @NonNull CampaignCreateModel model) {
    RestResponseWrapperModel<CampaignModel> result;
    try {
      result =
          campaignClientService.create(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              model);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException("Rest application has error");
    }

    return result.getData();
  }

  @Override
  public CampaignModel searchCampaign(@NonNull RequestContext ctx, @NonNull String extRef) {
    RestResponseWrapperModel<CampaignModel> result;
    try {
      result =
          campaignClientService.search(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              null,
              extRef);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException("Rest application has error");
    }

    return result.getData();
  }

  @Override
  public List<CampaignModel> availableCampaigns(@NonNull RequestContext ctx, @NonNull Long accountId) {
    RestResponseWrapperModel<List<CampaignModel>> result;
    try {
      result =
          campaignClientService.available(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              accountId);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException("Rest application has error");
    }

    return result.getData();
  }  

  @Override
  public List<CampaignMemberModel> addCampaignMembers(
      @NonNull RequestContext ctx, @NonNull Long campaignId, @NonNull List<String> members) {
    RestResponseWrapperModel<List<CampaignMemberModel>> result;
    try {
      result =
          campaignClientService.createMembers(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              String.valueOf(campaignId),
              members);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException("Rest application has error");
    }

    return result.getData();
  }

  @Override
  public List<CampaignMemberModel> delCampaignMembers(
      @NonNull RequestContext ctx, @NonNull Long campaignId, @NonNull List<String> members) {
    RestResponseWrapperModel<List<CampaignMemberModel>> result;
    try {
      result =
          campaignClientService.deleteMembers(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              String.valueOf(campaignId),
              members);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException("Rest application has error");
    }

    return result.getData();
  }

  @Override
  public SimplifyApplicationModel getApplication(RequestContext ctx, Long applicationId) {
    RestResponseWrapperModel<SimplifyApplicationModel> result;
    try {
      if (Objects.nonNull(ctx.getLinkedTenantId())) {
        result =
            applicationClientService.applicationByTenant(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                ctx.getLinkedTenantId(),
                applicationId);
      } else {
        result =
            applicationClientService.application(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                applicationId);
      }
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return result.getData();
  }

  @Override
  public List<SimplifyAccountAppSettingsModel> getAccountAppSettings(
      RequestContext ctx, Long accountId) {
    RestResponseWrapperModel<List<SimplifyAccountAppSettingsModel>> result;
    try {
      if (Objects.nonNull(ctx.getLinkedTenantId())) {
        result =
            accountClientService.accountAppSettingsByTenant(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                ctx.getLinkedTenantId(),
                accountId);
      } else {
        result =
            accountClientService.accountAppSettings(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                accountId);
      }
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return result.getData();
  }

  @Override
  public List<SimplifyAccountAppSettingsModel> getAccountAppSettingsByApplicationId(
      RequestContext ctx, Long applicationId, Long accountId) {
    RestResponseWrapperModel<List<SimplifyAccountAppSettingsModel>> result;
    try {
      result =
          applicationClientService.applicationSettings(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              applicationId,
              accountId,
              Boolean.TRUE);
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return result.getData();
  }

  @Override
  public SimpleAccountModel getAccount(RequestContext ctx, Long accountId) {
    SimpleAccountModel accountModel = cacheService.getAccountInfo(accountId);

    if (Objects.nonNull(accountModel)) {
      return accountModel;
    }

    RestResponseWrapperModel<SimpleAccountModel> result;
    try {
      if (Objects.nonNull(ctx.getLinkedTenantId())) {
        result =
            accountClientService.accountByTenant(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                ctx.getLinkedTenantId(),
                accountId);
      } else {
        result =
            accountClientService.account(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                accountId);
      }
    } catch (WebApplicationException e) {
      throw error(e);
    }

    cacheService.putAccountInfo(accountId, result.getData());

    return result.getData();
  }

  @Override
  public SimpleUserModel getUser(RequestContext ctx, Long accountId) {
    RestResponseWrapperModel<List<SimpleUserModel>> results;
    try {
      results =
          userClientService.users(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              accountId);
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return results.getData().get(0);
  }

  @Override
  public SimpleWalletModel getWallet(RequestContext ctx, Long accountId) {
    RestResponseWrapperModel<List<SimpleWalletModel>> result;
    try {
      result =
          walletClientService.wallet(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              accountId);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.getData().size() != 1) {
      throw new ApplicationException(
          "Unable to retrieve account wallet, the wallets size is not exactly 1. [%s] - [%s]",
          accountId, result.getData().size());
    }

    return result.getData().get(0);
  }

  @Override
  public SimpleItemModel getItem(RequestContext ctx, Long itemId) {
    RestResponseWrapperModel<SimpleItemModel> result;
    try {
      result =
          itemClientService.item(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              itemId);
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return result.getData();
  }

  @Override
  public List<SimpleItemModel> getItems(RequestContext ctx, Long vendorId) {
    RestResponseWrapperModel<List<SimpleItemModel>> result;
    try {
      result =
          itemClientService.itemsByVendor(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              vendorId);
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return result.getData();
  }

  @Override
  public SimpleApplicationItemModel getAppItem(RequestContext ctx, Long appItemId) {
    RestResponseWrapperModel<SimpleApplicationItemModel> result;
    try {
      result =
          itemClientService.appItem(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              appItemId);
    } catch (WebApplicationException e) {
      throw error(e);
    }
    return result.getData();
  }

  @Override
  public SimpleAccountModel getAccountByExtRef(RequestContext ctx, String extRef) {
    RestResponseWrapperModel<List<SimpleAccountModel>> result;
    try {
      result =
          accountClientService.accountsByExtRef(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              extRef);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (Objects.isNull(result.getData()) || result.getData().size() != 1) {
      throw new ApplicationException(
          "Unable to find account by ext-ref. [%s - %s]", extRef, result.getData());
    }

    return result.getData().get(0);
  }

  @Override
  public String extWalletLaunch(
      RequestContext ctx,
      String token,
      Long applicationId,
      Long itemId,
      Boolean demo,
      String lobbyUrl,
      String bankUrl) {
    return extWalletLaunch(ctx, token, applicationId, itemId, demo, lobbyUrl, bankUrl, null);
  }

  @Override
  public String extWalletLaunch(
      RequestContext ctx,
      String token,
      Long applicationId,
      Long itemId,
      Boolean demo,
      String lobbyUrl,
      String bankUrl,
      String callerIp) {
    RestResponseWrapperModel<String> result;
    try {
      SimplifyLauncherItemModel rq = new SimplifyLauncherItemModel();
      rq.setToken(token);
      rq.setAppId(applicationId);
      rq.setItemId(itemId);
      rq.setDemo(demo);
      rq.setExternal(Boolean.TRUE);

      if (!CommonUtils.isEmptyOrNull(lobbyUrl) || !CommonUtils.isEmptyOrNull(bankUrl)) {
        rq.setConfParams(Maps.newHashMap());
      }

      if (!CommonUtils.isEmptyOrNull(lobbyUrl)) {
        rq.getConfParams().put("lobby_url", lobbyUrl);
      }

      if (!CommonUtils.isEmptyOrNull(bankUrl)) {
        rq.getConfParams().put("bank_url", bankUrl);
      }

      if (!CommonUtils.isEmptyOrNull(ctx.getLanguage()) || !CommonUtils.isEmptyOrNull(callerIp)) {
        rq.setCtx(Maps.newHashMap());
      }

      if (!CommonUtils.isEmptyOrNull(ctx.getLanguage())) {
        rq.getCtx().put(Constant.LAUNCHER_META_DATA_KEY_LANG, ctx.getLanguage());
      }

      if (!CommonUtils.isEmptyOrNull(callerIp)) {
        Map<String, Object> meta = Maps.newHashMap(ctx.getMetaData());
        meta.put(Constant.LAUNCHER_META_DATA_KEY_GAME_ID, itemId);
        meta.put(Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS, callerIp);
        rq.getCtx().put(Constant.LAUNCHER_META_DATA_KEY_META_DATA, meta);
      }

      result =
          launcherClientService.launch(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              rq);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException(
          200,
          (Integer) result.getError().getOrDefault("code", 500),
          "Unable to call remote services, un-classified error arises = [%s]",
          result.getError().getOrDefault("message", "Error arises but unable to find details"));
    }

    return result.getData();
  }

  @Override
  public String extWalletPlaycheck(RequestContext ctx, Long transactionId) {
    RestResponseWrapperModel<String> result;
    try {
      result =
          launcherClientService.playcheck(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              transactionId,
              ctx.getLanguage());
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException(
          200,
          (Integer) result.getError().getOrDefault("code", 500),
          "Unable to call remote services, un-classified error arises = [%s]",
          result.getError().getOrDefault("message", "Error arises but unable to find details"));
    }

    return result.getData();
  }

  @Override
  public TransactionRoundModel findTransactionRoundByRoundExtRef(
      RequestContext ctx, String externalRef) {
    RestResponseWrapperModel<List<TransactionRoundModel>> result = null;
    try {
      result =
          feedTransactionClientService.getTransactionRoundByExternalRef(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              externalRef);
    } catch (WebApplicationException e) {
      throw error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException(
          200,
          (Integer) result.getError().getOrDefault("code", 500),
          "Unable to call remote services, un-classified error arises = [%s]",
          result.getError().getOrDefault("message", "Error arises but unable to find details"));
    }

    return result.getData().get(0);
  }

  @Override
  public TransactionModel findTransaction(RequestContext context, Long accountId, String txExtRef) {
    try {
      return transactionClientService.getTransaction(
          CommonUtils.authorizationBearer(context.getAccessToken()),
          context.getTimezone(),
          context.getCurrency(),
          context.getUuid().toString(),
          context.getLanguage(),
          accountId,
          txExtRef);
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  @Override
  public TransactionFeedModel findTransactionFeedById(RequestContext context, Long txId) {
    try {
      RestResponseWrapperModel<TransactionFeedModel> result =
          feedTransactionClientService.getTransactionById(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              txId);
      if (!result.hasData()) {
        throw new EntityNotExistException(404, "Empty data return.");
      }

      return result.getData();
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  @Override
  public TransactionFeedModel findTransactionFeedByExtRef(RequestContext context, String txExtRef) {
    try {
      RestResponseWrapperModel<List<TransactionFeedModel>> results =
          feedTransactionClientService.getTransactionByExternalRef(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              txExtRef);
      if (results.getData().isEmpty()) {
        throw new EntityNotExistException(404, "Empty list return.");
      }

      if (results.getData().size() != 1) {
        throw new DuplicateException(HttpStatus.SC_CONFLICT, "Multiple list returns");
      }

      return results.getData().get(0);
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  @Override
  public TransactionFeedModel findTransactionFeedByExtRef(
      RequestContext context, String txExtRef, TxCategory category) {
    try {
      RestResponseWrapperModel<List<TransactionFeedModel>> results =
          feedTransactionClientService.getTransactionByExternalRef(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              txExtRef);
      if (results.getData().isEmpty()) {
        throw new EntityNotExistException(404, "Empty list return.");
      }

      return results.getData().stream()
          .filter(data -> data.getCategory().equals(category.getCode()))
          .findFirst()
          .orElseThrow(
              () ->
                  new EntityNotExistException(
                      404, String.format("Tx by [%s] category is not found", category.getCode())));
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  @Override
  public TransactionRoundModel findTransactionRound(
      RequestContext context, Long accountId, String txExtRef) {
    try {
      return transactionClientService
          .getTransactionRound(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              accountId,
              txExtRef)
          .getData();
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  @Override
  public List<TransactionFeedModel> findTransactionFeedByRoundId(
      RequestContext context,
      Long applicationId,
      Long accountId,
      String game,
      Long roundId,
      Long dataTimeLimitInSeconds) {
    try {
      RestResponseWrapperModel<TransactionRoundModel> roundRs =
          feedTransactionClientService.getTransactionRoundByRoundId(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              roundId);
      if (!roundRs.hasData()) {
        throw new EntityNotExistException(404, "Empty data return.");
      }

      if (roundRs.getData().getApplicationId().longValue() != applicationId.longValue()) {
        throw new EntityNotExistException(404, "No data matching the application id.");
      }

      if (roundRs.getData().getAccountId().longValue() != accountId.longValue()) {
        throw new EntityNotExistException(404, "No data matching the account id.");
      }

      String roundRsExtItemId = "";
      if (Objects.nonNull(roundRs.getData().getMetaData())) {
        roundRsExtItemId =
            roundRs.getData().getMetaData().getOrDefault("ext_item_id", "").toString();
      }

      if (!roundRsExtItemId.equals(game)) {
        throw new EntityNotExistException(404, "No data matching the game.");
      }

      if (Objects.nonNull(dataTimeLimitInSeconds) && dataTimeLimitInSeconds > 0L) {
        Date now = new Date();
        Long diffInSeconds =
            TimeUnit.MILLISECONDS.toSeconds(
                now.getTime() - roundRs.getData().getStartTime().getTime());
        if (diffInSeconds
            > dataTimeLimitInSeconds) { // start time cannot be later than dataTimeLimitInSeconds
          throw new EntityNotExistException(404, "No data matching the data validity time frame.");
        }
      }

      if (Objects.nonNull(roundRs.getData().getTransactionIds())
          && !roundRs.getData().getTransactionIds().isEmpty()) {
        String txIds =
            roundRs.getData().getTransactionIds().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        RestResponseWrapperModel<List<TransactionFeedModel>> result =
            feedTransactionClientService.getTransactionByTxIds(
                CommonUtils.authorizationBearer(context.getAccessToken()),
                context.getTimezone(),
                context.getCurrency(),
                context.getUuid().toString(),
                context.getLanguage(),
                txIds);
        if (!result.hasData()) {
          throw new EntityNotExistException(404, "Empty data return.");
        }
        return result.getData();
      }
      return Lists.newArrayList();
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  @Override
  public List<TransactionRoundModel> findTransactionRound(
      RequestContext context,
      Long applicationId,
      Long accountId,
      Date startTime,
      Date endTime,
      Integer pageSize,
      Integer page) {
    try {
      RestResponseWrapperModel<List<TransactionRoundModel>> result =
          feedTransactionClientService.getTransactionRoundByAccountIdAndTime(
              CommonUtils.authorizationBearer(context.getAccessToken()),
              context.getTimezone(),
              context.getCurrency(),
              context.getUuid().toString(),
              context.getLanguage(),
              applicationId,
              accountId,
              FORMAT.format(startTime),
              FORMAT.format(endTime),
              pageSize,
              page);
      if (!result.hasData()) {
        throw new EntityNotExistException(404, "Empty data return.");
      }

      return result.getData();
    } catch (WebApplicationException e) {
      throw error(e);
    }
  }

  /**
   * @param e
   * @return
   */
  protected static BaseException error(WebApplicationException e) {
    String eMsg = null;
    ErrorModel model = null;

    try {
      eMsg = e.getResponse().readEntity(String.class);
    } catch (Exception ex) {
      // don't log any error, try best only to decode, but if failed, won't do anything.
    }

    try {
      model = CommonUtils.jsonRead(ErrorModel.class, eMsg);
    } catch (Exception ex) {
      // don't log any error, try best only to decode, but if failed, won't do anything.
    }

    if (Objects.nonNull(model)) {
      if (HttpStatus.SC_FORBIDDEN == e.getResponse().getStatus()
          || HttpStatus.SC_UNAUTHORIZED == e.getResponse().getStatus()) {
        return new AuthException(
            e.getResponse().getStatus(),
            model.getError().getCode(),
            "Unable to call remote services, authentication/authorization error arises = [%s]",
            model.getError().getMessage());
      } else if (HttpStatus.SC_PAYMENT_REQUIRED == e.getResponse().getStatus()) {
        return new PaymentException(
            e.getResponse().getStatus(),
            model.getError().getCode(),
            "Unable to call remote services, payment error arises = [%s]",
            model.getError().getMessage());
      } else if (HttpStatus.SC_CONFLICT == e.getResponse().getStatus()) {
        return new DuplicateException(
            e.getResponse().getStatus(),
            model.getError().getCode(),
            "Unable to call remote services, conflict error arises = [%s]",
            model.getError().getMessage());
      } else if (HttpStatus.SC_NOT_FOUND == e.getResponse().getStatus()) {
        return new EntityNotExistException(
            e.getResponse().getStatus(),
            model.getError().getCode(),
            "Unable to call remote services, entity not found error arises = [%s]",
            model.getError().getMessage());
      } else if (429 == e.getResponse().getStatus()) {
        return new TooManyRequestsException(
            e.getResponse().getStatus(),
            model.getError().getCode(),
            "Unable to call remote services, too many request error arises = [%s]",
            model.getError().getMessage());
      } else {
        return new ApplicationException(
            e.getResponse().getStatus(),
            model.getError().getCode(),
            "Unable to call remote services, un-classified error arises = [%s]",
            model.getError().getMessage());
      }
    } else {
      if (HttpStatus.SC_FORBIDDEN == e.getResponse().getStatus()
          || HttpStatus.SC_UNAUTHORIZED == e.getResponse().getStatus()) {
        return new AuthException(
            e.getResponse().getStatus(),
            "Unable to call remote services, authentication/authorization error arises");
      } else if (HttpStatus.SC_PAYMENT_REQUIRED == e.getResponse().getStatus()) {
        return new PaymentException(
            e.getResponse().getStatus(), "Unable to call remote services, payment error arises");
      } else if (HttpStatus.SC_CONFLICT == e.getResponse().getStatus()) {
        return new DuplicateException(
            e.getResponse().getStatus(), "Unable to call remote services, conflict error arises");
      } else if (HttpStatus.SC_NOT_FOUND == e.getResponse().getStatus()) {
        return new EntityNotExistException(
            e.getResponse().getStatus(),
            "Unable to call remote services, entity not found error arises");
      } else if (429 == e.getResponse().getStatus()) {
        return new TooManyRequestsException(
            e.getResponse().getStatus(),
            "Unable to call remote services, too many request error arises");
      } else {
        return new ApplicationException(
            e.getResponse().getStatus(),
            "Unable to call remote services, un-classified error arises");
      }
    }
  }
}
