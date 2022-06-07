package com.dashur.integration.commons;

import com.dashur.integration.commons.VendorConfig.VendorInfo;
import com.dashur.integration.commons.data.AuthResponse;
import com.dashur.integration.commons.data.BalanceResponse;
import com.dashur.integration.commons.data.SubTxCategory;
import com.dashur.integration.commons.data.TransactionFeedResponse;
import com.dashur.integration.commons.data.TransactionResponse;
import com.dashur.integration.commons.data.TransactionRoundResponse;
import com.dashur.integration.commons.data.TxCategory;
import com.dashur.integration.commons.rest.model.CampaignMemberModelExt;
import com.dashur.integration.commons.rest.model.CampaignVoucherModelExt;
import com.dashur.integration.commons.utils.CommonUtils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** commons integration service interface */
public interface CommonService {

  /**
   * Setup the context and also authenticates the call
   *
   * @param vendor The config for the vendor
   * @param token The token received from the call
   * @param locale The local to use, if none is provided Locale from hashtoken will be used or
   *     default to Local.EN if hash token isn't available
   * @param platform The platform that should be used to find apps client id and secret. If it is
   *     null we will try to get it from the hash token if the token is a valid hash token
   * @return A request context that can be used for other calls
   */
  RequestContext context(VendorInfo vendor, String token, Locale locale, String platform);

  /**
   * Setup the context for call using player_id
   *
   * @param vendor The config for the vendor
   * @param userId The user id received from the call
   * @param platform The platform that should be used to find apps client id and secret. If it is
   *     null we will use default platform from vendor info
   * @return A request context that can be used for other calls
   */
  RequestContext context(VendorInfo vendor, String userId, String platform);

  /**
   * Setup the context for integration api client
   *
   * @param clientId The api client id
   * @param clientPassword The api client password
   * @return A request context that can be used for domain services
   */
  RequestContext context(String clientId, String clientPassword);

  /**
   * Authenticate vendor
   *
   * @param vendor The config for the vendor
   * @param callerIp The caller IP
   */
  void authenticateVendor(VendorInfo vendor, String callerIp);

  /**
   * Authenticate vendor
   *
   * @param vendor The config for the vendor
   * @param auth Authorization type (basic, bearer, etc..)
   * @param callerIp The caller IP
   */
  void authenticateVendor(VendorInfo vendor, String auth, String callerIp);

  /**
   * Process authentication request and get balance. This is a bit of a dummy as the proper auth is
   * done in context call
   *
   * @param ctx The request context
   * @return An auth response with current balance
   */
  AuthResponse authenticate(RequestContext ctx);

  /**
   * Process balance request
   *
   * @param ctx The request context
   * @return A balance response
   */
  BalanceResponse balance(RequestContext ctx);

  /**
   * Process wager request
   *
   * @param ctx The request context
   * @param txExtRef The tx id from integrating system
   * @param txExtRoundRef The tx round id from integrating system
   * @param gameRef The game reference from integrating system
   * @param amount The amount from integrating system, the currency comes from the ctx
   * @param bonusAmount The wager amount to be deducted from bonus balance
   * @param poolAmount The jackpot contribution amount; part of players bet which go to jackpot pool
   * @param subCategory The sub category derived from integrating system
   * @param metadata Any vendor specific meta data to store with the tx
   * @return A tx response
   */
  TransactionResponse wager(
      RequestContext ctx,
      String txExtRef,
      String txExtRoundRef,
      String gameRef,
      BigDecimal amount,
      BigDecimal bonusAmount,
      BigDecimal poolAmount,
      SubTxCategory subCategory,
      Map<String, Object> metadata);

  /**
   * Process payout request
   *
   * @param ctx The request context
   * @param txExtRef The tx id from integrating system
   * @param txExtRoundRef The tx round id from integrating system
   * @param gameRef The game reference from integrating system
   * @param amount The amount from integrating system, the currency comes from the ctx
   * @param bonusAmount The payout amount to be added to bonus balance
   * @param poolAmount The jackpot winning amount
   * @param subCategory The sub category derived from integrating system
   * @param metadata Any vendor specific meta data to store with the tx
   * @return A tx response
   */
  TransactionResponse payout(
      RequestContext ctx,
      String txExtRef,
      String txExtRoundRef,
      String gameRef,
      BigDecimal amount,
      BigDecimal bonusAmount,
      BigDecimal poolAmount,
      SubTxCategory subCategory,
      Map<String, Object> metadata);

  /**
   * Process endround request
   *
   * @param ctx The request context
   * @param txExtRef The tx id from integrating system
   * @param txExtRoundRef The tx round id from integrating system
   * @param gameRef The game reference from integrating system
   * @param optionalMeta Any vendor specific meta data to store with the tx
   * @return A tx response
   */
  TransactionResponse endround(
      RequestContext ctx,
      String txExtRef,
      String txExtRoundRef,
      String gameRef,
      Optional<Map<String, Object>> optionalMeta);

  /**
   * Process refund request without game details
   *
   * @param ctx The request context
   * @param originTxExtRef The tx id from integrating system to rollback
   * @return A tx response
   */
  TransactionResponse refund(RequestContext ctx, String originTxExtRef);

  /**
   * Process refund request
   *
   * @param ctx The request context
   * @param originTxExtRef The tx id from integrating system to rollback
   * @param txExtRoundRef The tx round id from integrating system
   * @param gameRef The game reference from integrating system
   * @param amount The amount from integrating system, the currency comes from the ctx
   * @return A tx response
   */
  TransactionResponse refund(
      RequestContext ctx,
      String originTxExtRef,
      String txExtRoundRef,
      String gameRef,
      BigDecimal amount);

  /**
   * Retrieve transaction by tx id from integrating system
   *
   * @param ctx The request context
   * @param txExtRef The tx id from integrating system
   * @return A tx response
   */
  TransactionResponse getTransaction(RequestContext ctx, String txExtRef);

  /**
   * @param ctx The request context
   * @param txId The tx id from feed round
   * @return A tx response
   */
  TransactionFeedResponse getTransactionFeed(RequestContext ctx, Long txId);

  /**
   * Retrieve transaction feed by tx id from integrating system
   *
   * @param ctx The request context
   * @param txExtRef The tx id from integrating system
   * @return A tx response
   */
  TransactionFeedResponse getTransactionFeed(
      RequestContext ctx, String txExtRef, TxCategory category);

  /**
   * Retrieve transaction feed by round id
   *
   * @param ctx The request context
   * @param txExtRoundRef The tx round id from integrating system
   * @return A tx round response
   */
  TransactionRoundResponse getTransactionFeedRound(RequestContext ctx, String txExtRoundRef);

  /**
   * Retrieve transaction round by game round id
   *
   * @param ctx The request context
   * @param txExtRoundRef The tx round id from integrating system
   * @return A tx round response
   */
  TransactionRoundResponse getTransactionRound(RequestContext ctx, String txExtRoundRef);

  /**
   * Validate campaign member assignment
   *
   * @param modelExt The campaign member extension model
   */
  void validateCampaignAssignment(CampaignMemberModelExt modelExt);

  /**
   * Cache campaign member assignment for validation purpose
   *
   * @param modelExt The campaign member extension model
   */
  void storeCampaignAssignment(CampaignMemberModelExt modelExt);

  /**
   * Validate campaign voucher claim
   *
   * @param modelExt
   */
  void validateCampaignVoucherClaim(CampaignVoucherModelExt modelExt);

  /**
   * Cache campaign voucher claim for validation purpose
   *
   * @param modelExt The campaign voucher extension model
   */
  void storeCampaignVoucherClaim(CampaignVoucherModelExt modelExt);

  /**
   * @param ctx
   * @param applicationId
   * @param game
   * @param roundId
   * @param dataTimeLimitInSeconds -> null or 0/negative -> unlimited
   * @return
   */
  List<TransactionFeedResponse> getTransactionFeedByRoundId(
      RequestContext ctx,
      Long applicationId,
      String game,
      Long roundId,
      Long dataTimeLimitInSeconds);

  /**
   * @param ctx
   * @param applicationId
   * @param game -> ext ref for item/game
   * @param startTime
   * @param endTime
   * @param pageSize
   * @param page
   * @param dataTimeLimitInSeconds -> null or 0/negative -> unlimited
   * @return Pair of results and next page
   */
  CommonUtils.Pair<List<TransactionRoundResponse>, Integer> getTransactionRound(
      RequestContext ctx,
      Long applicationId,
      String game,
      Date startTime,
      Date endTime,
      Integer pageSize,
      Integer page,
      Long dataTimeLimitInSeconds);
}
