package com.dashur.integration.commons.cache.impl;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.cache.CacheProvider;
import com.dashur.integration.commons.cache.CacheService;
import com.dashur.integration.commons.cache.model.ItemInfo;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/** Cache service, overall wrapping for underlying cache implementation. */
@Slf4j
@Singleton
public class CacheServiceImpl implements CacheService {
  @Inject CacheProvider cacheProvider;

  @PostConstruct
  public void init() {
    log.info("CacheService.init()");
    cacheProvider.initCache(
        Constant.CACHE_NAME_ACCESS_TOKEN, String.class, String.class, 100_000, 55);
    cacheProvider.initCache(
        Constant.CACHE_NAME_REFRESH_TOKEN, String.class, String.class, 100_000, 3 * 55);
    cacheProvider.initCache(
        Constant.CACHE_NAME_SESSION_TOKEN, String.class, String.class, 100_000, 2 * 55);
    cacheProvider.initCache(Constant.CACHE_NAME_CURRENCY, Long.class, String.class, 5_000, 60);
    cacheProvider.initCache(
        Constant.CACHE_NAME_AFFILIATE_ID, Long.class, String.class, 1_000, 24 * 60);
    cacheProvider.initCache(
        Constant.CACHE_NAME_CLIENT_APP_ACCESS_TOKEN, String.class, String.class, 1_000, 45);
    cacheProvider.initCache(
        Constant.CACHE_NAME_ITEM_INFO, String.class, String.class, 1_000, 24 * 60);
    cacheProvider.initCache(Constant.CACHE_NAME_ACCOUNT_INFO, Long.class, String.class, 1_000, 30);
    cacheProvider.initCache(
        Constant.CACHE_NAME_BET_LEVEL, String.class, String.class, 1_000, 24 * 60);
    cacheProvider.initCache(
        Constant.CACHE_NAME_GAME_PARAMS, String.class, String.class, 1_000, 3 * 60);
    cacheProvider.initCache(Constant.CACHE_NAME_GAME_CONFIG, String.class, String.class, 1_000, 30);
    cacheProvider.initCache(
        Constant.CACHE_NAME_CAMPAIGN_ASSIGNMENT, String.class, String.class, 10_000, 24 * 60);
    cacheProvider.initCache(
        Constant.CACHE_NAME_CAMPAIGN_VOUCHER, String.class, String.class, 50_000, 7 * 24 * 60);
  }

  @Override
  public String getRefreshToken(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_REFRESH_TOKEN, String.class, key);
  }

  @Override
  public String getAccessToken(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_ACCESS_TOKEN, String.class, key);
  }

  @Override
  public Optional<String> getAccessTokenOrEmpty(String key) {
    return Optional.of(this.cacheProvider.get(Constant.CACHE_NAME_ACCESS_TOKEN, String.class, key));
  }

  @Override
  public void putToken(String key, Token token) {
    putAccessToken(key, token.getAccessToken());
    putRefreshToken(key, token.getRefreshToken());
  }

  @Override
  public void putAccessToken(String key, String accessToken) {
    this.cacheProvider.put(Constant.CACHE_NAME_ACCESS_TOKEN, key, accessToken);
  }

  @Override
  public void putRefreshToken(String key, String refreshToken) {
    // for client credential token, there is no refresh token, so don't need to cache.
    if (!CommonUtils.isEmptyOrNull(refreshToken)) {
      this.cacheProvider.put(Constant.CACHE_NAME_REFRESH_TOKEN, key, refreshToken);
    }
  }

  @Override
  public String getSessionToken(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_SESSION_TOKEN, String.class, key);
  }

  @Override
  public void putSessionToken(String key, String sessionToken) {
    this.cacheProvider.put(Constant.CACHE_NAME_SESSION_TOKEN, key, sessionToken);
  }

  @Override
  public void removeSessionToken(String key) {
    this.cacheProvider.remove(Constant.CACHE_NAME_SESSION_TOKEN, key);
  }

  @Override
  public String getCurrency(Long key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_CURRENCY, String.class, key);
  }

  @Override
  public void putCurrency(Long accountId, String currency) {
    this.cacheProvider.put(Constant.CACHE_NAME_CURRENCY, accountId, currency);
  }

  @Override
  public void clearCurrencyCache() {
    this.cacheProvider.removeAll(Constant.CACHE_NAME_CURRENCY);
  }

  @Override
  public String getAffiliateId(Long accountId) {
    return this.cacheProvider.get(Constant.CACHE_NAME_AFFILIATE_ID, String.class, accountId);
  }

  @Override
  public void putAffiliateId(Long accountId, String affiliateId) {
    this.cacheProvider.put(Constant.CACHE_NAME_AFFILIATE_ID, accountId, affiliateId);
  }

  @Override
  public String getClientAppAccessToken(String clientAppId) {
    return this.cacheProvider.get(
        Constant.CACHE_NAME_CLIENT_APP_ACCESS_TOKEN, String.class, clientAppId);
  }

  @Override
  public void putClientAppAccessToken(String clientAppId, String accessToken) {
    this.cacheProvider.put(Constant.CACHE_NAME_CLIENT_APP_ACCESS_TOKEN, clientAppId, accessToken);
  }

  @Override
  public void clearAffiliateIdCache() {
    this.cacheProvider.removeAll(Constant.CACHE_NAME_AFFILIATE_ID);
  }

  @Override
  public void clearTokenCache() {
    this.cacheProvider.removeAll(Constant.CACHE_NAME_CLIENT_APP_ACCESS_TOKEN);
    this.cacheProvider.removeAll(Constant.CACHE_NAME_ACCESS_TOKEN);
    this.cacheProvider.removeAll(Constant.CACHE_NAME_REFRESH_TOKEN);
  }

  @Override
  public ItemInfo getItemInfo(Long campaignOwnerId, Long itemId) {
    String key = String.format("%s_%s", campaignOwnerId, itemId);
    String data = this.cacheProvider.get(Constant.CACHE_NAME_ITEM_INFO, String.class, key);

    if (!CommonUtils.isEmptyOrNull(data)) {
      return CommonUtils.jsonRead(ItemInfo.class, data);
    }

    return null;
  }

  @Override
  public void putItemInfo(Long campaignOwnerId, Long itemId, ItemInfo itemInfo) {
    String key = String.format("%s_%s", campaignOwnerId, itemId);
    this.cacheProvider.put(Constant.CACHE_NAME_ITEM_INFO, key, CommonUtils.jsonToString(itemInfo));
  }

  @Override
  public void clearItemInfoCache() {
    this.cacheProvider.removeAll(Constant.CACHE_NAME_ITEM_INFO);
  }

  @Override
  public void putAccountInfo(Long accountId, SimpleAccountModel accountModel) {
    this.cacheProvider.put(
        Constant.CACHE_NAME_ACCOUNT_INFO, accountId, CommonUtils.jsonToString(accountModel));
  }

  @Override
  public SimpleAccountModel getAccountInfo(Long accountId) {
    String data = this.cacheProvider.get(Constant.CACHE_NAME_ACCOUNT_INFO, String.class, accountId);

    if (!CommonUtils.isEmptyOrNull(data)) {
      return CommonUtils.jsonRead(SimpleAccountModel.class, data);
    }

    return null;
  }

  @Override
  public void clearAccountInfoCache() {
    this.cacheProvider.removeAll(Constant.CACHE_NAME_ACCOUNT_INFO);
  }

  @Override
  public String getBetLevelSetting(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_BET_LEVEL, String.class, key);
  }

  @Override
  public void putBetLevelSetting(String key, String value) {
    this.cacheProvider.put(Constant.CACHE_NAME_BET_LEVEL, key, value);
  }

  @Override
  public void clearBetLevelSetting() {
    this.cacheProvider.removeAll(Constant.CACHE_NAME_BET_LEVEL);
  }

  @Override
  public String getGameParams(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_GAME_PARAMS, String.class, key);
  }

  @Override
  public void putGameParams(String key, String value) {
    this.cacheProvider.put(Constant.CACHE_NAME_GAME_PARAMS, key, value);
  }

  @Override
  public String getGameConfig(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_GAME_CONFIG, String.class, key);
  }

  @Override
  public void putGameConfig(String key, String value) {
    this.cacheProvider.put(Constant.CACHE_NAME_GAME_CONFIG, key, value);
  }

  @Override
  public String getCampaignAssignment(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_CAMPAIGN_ASSIGNMENT, String.class, key);
  }

  @Override
  public void putCampaignAssignment(String key, String value) {
    this.cacheProvider.put(Constant.CACHE_NAME_CAMPAIGN_ASSIGNMENT, key, value);
  }

  @Override
  public String getCampaignVoucherClaim(String key) {
    return this.cacheProvider.get(Constant.CACHE_NAME_CAMPAIGN_VOUCHER, String.class, key);
  }

  @Override
  public void putCampaignVoucherClaim(String key, String value) {
    this.cacheProvider.put(Constant.CACHE_NAME_CAMPAIGN_VOUCHER, key, value);
  }
}
