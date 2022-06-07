package com.dashur.integration.commons.cache;

import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.cache.model.ItemInfo;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import java.util.Optional;

/** Cache service */
public interface CacheService {
  /**
   * get refresh token by short token id
   *
   * @param key
   * @return
   */
  String getRefreshToken(String key);

  /**
   * get access token by short token id
   *
   * @param key
   * @return
   */
  String getAccessToken(String key);

  /**
   * similar with getAccessToken but will return optinal.empty if access token is not found.
   *
   * @param key
   * @return
   */
  Optional<String> getAccessTokenOrEmpty(String key);

  /**
   * putting token by short token id key.
   *
   * @param key
   * @param token
   */
  void putToken(String key, Token token);

  /**
   * putting access token by id key.
   *
   * @param key
   * @param accessToken
   */
  void putAccessToken(String key, String accessToken);

  /**
   * putting refresh token by id key.
   *
   * @param key
   * @param refreshToken
   */
  void putRefreshToken(String key, String refreshToken);

  /**
   * get vendor session token by defined key
   *
   * @param key
   * @return
   */
  String getSessionToken(String key);

  /**
   * put vendor session token by defined key
   *
   * @param key
   * @param sessionToken
   */
  void putSessionToken(String key, String sessionToken);

  /**
   * remove vendor session token by defined key
   *
   * @param key
   */
  void removeSessionToken(String key);

  /**
   * get currency by accountId
   *
   * @param accountId
   * @return
   */
  String getCurrency(Long accountId);

  /**
   * putting accountId into currency
   *
   * @param accountId
   * @param currency
   */
  void putCurrency(Long accountId, String currency);

  /** clear currency cache */
  void clearCurrencyCache();

  /**
   * retrieve affiliate id from cache.
   *
   * @param accountId
   * @return
   */
  String getAffiliateId(Long accountId);

  /**
   * store affiliate id to cache.
   *
   * @param accountId
   * @param affiliateId
   */
  void putAffiliateId(Long accountId, String affiliateId);

  /** clear affiliate id cache */
  void clearAffiliateIdCache();

  /**
   * get client app access token by client app id
   *
   * @param clientAppId
   * @return
   */
  String getClientAppAccessToken(String clientAppId);

  /**
   * put client app access token
   *
   * @param clientAppId
   * @param accessToken
   */
  void putClientAppAccessToken(String clientAppId, String accessToken);

  /** clear the client app cache. */
  void clearTokenCache();

  /**
   * get item ext ref
   *
   * @param campaignOwnerId
   * @param itemId
   * @return
   */
  ItemInfo getItemInfo(Long campaignOwnerId, Long itemId);

  /**
   * put item ext ref (by item id)
   *
   * @param campaignOwnerId
   * @param itemId
   * @param itemInfo
   */
  void putItemInfo(Long campaignOwnerId, Long itemId, ItemInfo itemInfo);

  /** clearing item ext-ref */
  void clearItemInfoCache();

  /**
   * cache account model by accountId
   *
   * @param accountId
   * @param accountModel
   */
  void putAccountInfo(Long accountId, SimpleAccountModel accountModel);

  /**
   * get the cached account model by accountId
   *
   * @param accountId
   * @return
   */
  SimpleAccountModel getAccountInfo(Long accountId);

  /** clear the account info cache. */
  void clearAccountInfoCache();

  /**
   * get bet level settings by key
   *
   * @param key
   * @return
   */
  String getBetLevelSetting(String key);

  /**
   * cache get level settings by key
   *
   * @param key
   * @param value
   */
  void putBetLevelSetting(String key, String value);

  /** */
  void clearBetLevelSetting();

  /**
   * get the cached game params by specified key
   *
   * @param key
   * @return
   */
  String getGameParams(String key);

  /**
   * cache game params by key with longer expiry
   *
   * @param key
   * @param value
   */
  void putGameParams(String key, String value);

  /**
   * get the cached game config by specified key
   *
   * @param key
   * @return
   */
  String getGameConfig(String key);

  /**
   * cache game config by key with shorter expiry
   *
   * @param key
   * @param value
   */
  void putGameConfig(String key, String value);

  /**
   * get the cached campaign member assignment by specified key
   *
   * @param key
   * @return
   */
  String getCampaignAssignment(String key);

  /**
   * cache campaign member assignment by key
   *
   * @param key
   * @param value
   */
  void putCampaignAssignment(String key, String value);

  /**
   * get the cached campaign voucher claim by specified key
   *
   * @param key
   * @return
   */
  String getCampaignVoucherClaim(String key);

  /**
   * cache campaign voucher claim by key
   *
   * @param key
   * @param value
   */
  void putCampaignVoucherClaim(String key, String value);
}
