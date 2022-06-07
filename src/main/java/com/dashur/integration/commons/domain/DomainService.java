package com.dashur.integration.commons.domain;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.data.TxCategory;
import com.dashur.integration.commons.domain.model.AccountBalance;
import com.dashur.integration.commons.domain.model.Transaction;
import com.dashur.integration.commons.domain.model.TransactionCreateRequest;
import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignMemberModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.CampaignUpdateModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.SimpleApplicationItemModel;
import com.dashur.integration.commons.rest.model.SimpleItemModel;
import com.dashur.integration.commons.rest.model.SimpleUserModel;
import com.dashur.integration.commons.rest.model.SimpleWalletModel;
import com.dashur.integration.commons.rest.model.SimplifyAccountAppSettingsModel;
import com.dashur.integration.commons.rest.model.SimplifyApplicationModel;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import com.dashur.integration.commons.rest.model.TransactionModel;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import java.util.Date;
import java.util.List;

/** Domain services. A wrapper to rest api and ensure the transition to application model. */
public interface DomainService {
  /**
   * @param context
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppICredential
   * @return
   */
  Token companyLogin(
      RequestContext context,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppICredential);

  /**
   * refreshing token.
   *
   * @param context
   * @param refreshToken
   * @param clientId
   * @param clientCredential
   * @return
   */
  Token refreshToken(
      RequestContext context, String refreshToken, String clientId, String clientCredential);

  /**
   * login app client
   *
   * @param context
   * @param clientId
   * @param clientCredential
   * @return
   */
  Token loginAppClient(RequestContext context, String clientId, String clientCredential);

  /**
   * login as member
   *
   * @param context
   * @param userId
   * @return
   */
  Token loginAsMember(RequestContext context, Long userId);

  /**
   * get account balance
   *
   * @param context
   * @return
   */
  AccountBalance getAccountBalance(RequestContext context);

  /**
   * get account currency
   *
   * @param context
   * @return
   */
  String getWalletCurrency(RequestContext context);

  /**
   * @param context
   * @param txRq
   * @return
   */
  List<Transaction> createTransaction(RequestContext context, List<TransactionCreateRequest> txRq);

  /**
   * Do a full refund of a transaction by looking up the original and then creating a refund for it
   *
   * @param context The request context
   * @param txExtRef The external reference of the transaction id to rollback
   * @return The rollback transaction
   */
  Transaction refundTransaction(RequestContext context, String txExtRef);

  /**
   * Get Application from dashur backend.
   *
   * @param ctx
   * @param applicationId
   * @return
   */
  SimplifyApplicationModel getApplication(RequestContext ctx, Long applicationId);

  /**
   * Get list of account application from dashur backend.
   *
   * @param ctx
   * @param accountId
   * @return
   */
  List<SimplifyAccountAppSettingsModel> getAccountAppSettings(RequestContext ctx, Long accountId);

  /**
   * Get list of account application from dashur backend.
   *
   * @param ctx
   * @param accountId
   * @return
   */
  List<SimplifyAccountAppSettingsModel> getAccountAppSettingsByApplicationId(
      RequestContext ctx, Long applicationId, Long accountId);

  /**
   * Get Account from dashur backend.
   *
   * @param ctx
   * @param accountId
   * @return
   */
  SimpleAccountModel getAccount(RequestContext ctx, Long accountId);

  /**
   * Get User from dashur backend
   *
   * @param ctx
   * @param accountId
   * @return
   */
  SimpleUserModel getUser(RequestContext ctx, Long accountId);

  /**
   * Get Wallet from dashur backend
   *
   * @param ctx
   * @param accountId
   * @return
   */
  SimpleWalletModel getWallet(RequestContext ctx, Long accountId);

  /**
   * Get Item from dashur backend
   *
   * @param ctx
   * @param itemId
   * @return
   */
  SimpleItemModel getItem(RequestContext ctx, Long itemId);

  /**
   * Get a list of Items by vendor from dashur backend
   *
   * @param ctx
   * @param vendorId
   * @return
   */
  List<SimpleItemModel> getItems(RequestContext ctx, Long vendorId);

  /**
   * Get AppItem by id from dashur backend.
   *
   * @param ctx
   * @param appItemId
   * @return
   */
  SimpleApplicationItemModel getAppItem(RequestContext ctx, Long appItemId);

  /**
   * @param ctx
   * @param extRef
   * @return
   */
  SimpleAccountModel getAccountByExtRef(RequestContext ctx, String extRef);

  /**
   * get a launch game url.
   *
   * @param ctx
   * @param token
   * @param applicationId
   * @param itemId
   * @param demo
   * @param lobbyUrl
   * @param bankUrl
   * @return
   */
  String extWalletLaunch(
      RequestContext ctx,
      String token,
      Long applicationId,
      Long itemId,
      Boolean demo,
      String lobbyUrl,
      String bankUrl);

  /**
   * get a launch game url.
   *
   * @param ctx
   * @param token
   * @param applicationId
   * @param itemId
   * @param demo
   * @param lobbyUrl
   * @param bankUrl
   * @return
   */
  String extWalletLaunch(
      RequestContext ctx,
      String token,
      Long applicationId,
      Long itemId,
      Boolean demo,
      String lobbyUrl,
      String bankUrl,
      String callerIp);

  /**
   * get a playcheck url
   *
   * @param ctx
   * @param transactionId
   * @return
   */
  String extWalletPlaycheck(RequestContext ctx, Long transactionId);

  /**
   * @param context\
   * @param model
   * @return
   */
  CampaignModel createCampaign(RequestContext context, CampaignCreateModel model);

  /**
   * @param context
   * @param campaignId
   * @param model
   * @return
   */
  CampaignModel updateCampaign(RequestContext context, Long campaignId, CampaignUpdateModel model);

  /**
   * @param context
   * @param extRef
   * @return
   */
  CampaignModel searchCampaign(RequestContext context, String extRef);

  /**
   * @param context
   * @param campaignId
   * @param members
   * @return
   */
  List<CampaignMemberModel> addCampaignMembers(
      RequestContext context, Long campaignId, List<String> members);

  /**
   * @param context
   * @param campaignId
   * @param members
   * @return
   */
  List<CampaignMemberModel> delCampaignMembers(
      RequestContext context, Long campaignId, List<String> members);

  /**
   * @param context
   * @param externalRef
   * @return
   */
  TransactionRoundModel findTransactionRoundByRoundExtRef(
      RequestContext context, String externalRef);

  /**
   * Get a transaction by account_id and external_ref
   *
   * @param context The request context
   * @param accountId The account id
   * @param txExtRef The external reference of the transaction
   * @return
   */
  TransactionModel findTransaction(RequestContext context, Long accountId, String txExtRef);

  /**
   * Get a transaction from tx-feed
   *
   * @param context The request context
   * @param txId The id of the transaction
   * @return
   */
  TransactionFeedModel findTransactionFeedById(RequestContext context, Long txId);

  /**
   * Get a transaction from tx-feed
   *
   * @param context The request context
   * @param txExtRef The external reference of the transaction
   * @return
   */
  TransactionFeedModel findTransactionFeedByExtRef(RequestContext context, String txExtRef);

  /**
   * Get a transaction from tx-feed by category
   *
   * @param context The request context
   * @param txExtRef The external reference of the transaction
   * @param category The category of the transaction
   * @return
   */
  TransactionFeedModel findTransactionFeedByExtRef(
      RequestContext context, String txExtRef, TxCategory category);

  /**
   * Get a transaction round by account_id and external_ref
   *
   * @param context The request context
   * @param accountId The account id
   * @param txExtRef The external reference of the transaction (round_id)
   * @return
   */
  TransactionRoundModel findTransactionRound(
      RequestContext context, Long accountId, String txExtRef);

  /**
   * find list of transaction list given the round id. for that member account.
   *
   * @param context
   * @param roundId
   * @return
   */
  List<TransactionFeedModel> findTransactionFeedByRoundId(
      RequestContext context,
      Long applicationId,
      Long accountId,
      String game,
      Long roundId,
      Long dataTimeLimitInSeconds);

  /**
   * find list of transaction round list given the account id, start & end time.
   *
   * @param context
   * @param accountId
   * @param startTime
   * @param endTime
   * @param pageSize
   * @param page
   * @return
   */
  List<TransactionRoundModel> findTransactionRound(
      RequestContext context,
      Long applicationId,
      Long accountId,
      Date startTime,
      Date endTime,
      Integer pageSize,
      Integer page);
}
