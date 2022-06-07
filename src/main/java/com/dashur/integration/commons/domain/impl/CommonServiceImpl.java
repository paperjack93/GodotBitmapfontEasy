package com.dashur.integration.commons.domain.impl;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.auth.HashToken;
import com.dashur.integration.commons.auth.RefreshToken;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.cache.CacheService;
import com.dashur.integration.commons.domain.CommonService;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.Locale;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CommonServiceImpl implements CommonService {
  @Inject CacheService cacheService;

  @Inject DomainService domainService;

  @Override
  public RequestContext populateCtx(
      RequestContext inCtx,
      String clientId,
      String clientCredentials,
      String playerToken,
      IsTestUser isTestUser) {
    String refreshToken = playerToken;
    HashToken hashToken;
    Token token;

    Locale locale;
    String platform;

    if (HashToken.isHashToken(refreshToken)) {
      hashToken = HashToken.parse(refreshToken);

      // if incoming context contain language, will used it, else will take from hash token.
      if (!CommonUtils.isEmptyOrNull(inCtx.getLanguage())) {
        locale = new Locale(inCtx.getLanguage());
      } else {
        locale = hashToken.getLanguage();
      }

      // if incoming context contain platform, will used it, else will take from hash token.
      if (!CommonUtils.isEmptyOrNull(inCtx.getPlatform())) {
        platform = inCtx.getPlatform();
      } else {
        platform = hashToken.getPlatform();
      }

      // if its hash token, then will get from cache the refresh token
      refreshToken = cacheService.getRefreshToken(hashToken.getCacheKey());

      if (CommonUtils.isEmptyOrNull(refreshToken)) {
        throw new AuthException(
            "Unable to get refresh token base on hash token. It might be expired.");
      }

      if (isTestUser.isTestUser(hashToken.getUsername())) {
        log.info(
            "[{}], seq:{}, token from cache: [{}] - [{}]",
            hashToken.getUsername(),
            inCtx.getUuid(),
            cacheService.getAccessToken(hashToken.getCacheKey()),
            refreshToken);
      }
    } else {
      if (CommonUtils.isEmptyOrNull(inCtx.getLanguage())) {
        throw new ValidationException("Unable to get language from context.");
      }
      if (CommonUtils.isEmptyOrNull(inCtx.getPlatform())) {
        throw new ValidationException("Unable to get platform from context.");
      }

      // if its a non hash-token, then get the locale & channel from incoming context;
      locale = new Locale(inCtx.getLanguage());
      platform = inCtx.getPlatform();
    }

    token = domainService.refreshToken(inCtx, refreshToken, clientId, clientCredentials);
    RefreshToken parsedRefreshToken = RefreshToken.parse(token.getRefreshToken());
    long linkedTenantId = parsedRefreshToken.getLinkedTenantId();
    hashToken = HashToken.fromRefreshToken(platform, locale, parsedRefreshToken);
    cacheService.putToken(hashToken.getCacheKey(), token);

    if (isTestUser.isTestUser(hashToken.getUsername())) {
      log.info(
          "[{}], seq:{}, token from dashur: [{}] - [{}]",
          hashToken.getUsername(),
          inCtx.getUuid(),
          token.getAccessToken(),
          token.getRefreshToken());
    }

    RequestContext ctx =
        inCtx
            .withAccessToken(token.getAccessToken())
            .withHashToken(hashToken)
            .withUsername(hashToken.getUsername())
            .withAccountId(hashToken.getAccountId())
            .withUserId(hashToken.getUserId())
            .withPlatform(hashToken.getPlatform())
            .withLanguage(locale.toString())
            .withApplicationId(parsedRefreshToken.getApplicationId())
            .withTenantId(parsedRefreshToken.getTenantId())
            .withAccountPath(parsedRefreshToken.getAccountPath())
            .withLinkedTenantId(linkedTenantId == 0 ? null : linkedTenantId);

    String currency = domainService.getWalletCurrency(ctx);
    return ctx.withCurrency(currency);
  }

  @Override
  public RequestContext refreshCtxOrForceRefreshToken(
      RequestContext ctx,
      String appId,
      String appCredentials,
      String clientId,
      String clientCredentials) {
    if (Objects.nonNull(ctx.getAccessToken()) && Objects.nonNull(ctx.getHashToken())) {
      if (!CommonUtils.isTokenExpired(ctx.getAccessToken())) {
        // token not expired. won't do anything.
        return ctx;
      }

      boolean isToPerformAppRefreshToken = Boolean.FALSE;

      // token is expired, so will be trying to refresh token.
      String ctxRefreshToken = cacheService.getRefreshToken(ctx.getHashToken().getCacheKey());

      if (CommonUtils.isEmptyOrNull(ctxRefreshToken)
          || CommonUtils.isTokenExpired(ctxRefreshToken)) {
        // ctx refersh token is expired, so will need to force login .
        // do a login using api client to refresh expired member token
        String accessToken =
            clientAppAccessToken(RequestContext.instance(), clientId, clientCredentials);
        Token token =
            domainService.loginAsMember(
                RequestContext.instance().withAccessToken(accessToken), ctx.getUserId());
        // refresh token from force login will still need to be refreshed using app credentials.
        ctxRefreshToken = token.getRefreshToken();
      }

      // if refresh token is not null & is not expired, will go tru method
      // to refresh its token.
      Token token = domainService.refreshToken(ctx, ctxRefreshToken, appId, appCredentials);
      RefreshToken parsedRefreshToken = RefreshToken.parse(token.getRefreshToken());
      HashToken hashToken =
          HashToken.fromRefreshToken(
              ctx.getPlatform(), ctx.getHashToken().getLanguage(), parsedRefreshToken);
      cacheService.putToken(hashToken.getCacheKey(), token);
      return ctx.withAccessToken(token.getAccessToken()).withHashToken(hashToken);
    }

    throw new ValidationException("Context doesn't contain accessToken & hashToken.");
  }

  @Override
  public String clientAppAccessToken(
      RequestContext ctx, String clientAppId, String clientAppCredentials) {
    String accessToken = cacheService.getClientAppAccessToken(clientAppId);

    if (CommonUtils.isEmptyOrNull(accessToken)) {
      Token token = domainService.loginAppClient(ctx, clientAppId, clientAppCredentials);
      accessToken = token.getAccessToken();
      cacheService.putClientAppAccessToken(clientAppId, accessToken);
    }

    return accessToken;
  }

  @Override
  public String companyAppAccessToken(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential) {
    String accessTokenKey = String.format("CompanyAccessToken:%s", companyAppId);
    String refreshTokenKey = String.format("CompanyRefreshToken:%s", companyAppId);
    String refreshToken = cacheService.getRefreshToken(refreshTokenKey);
    String accessToken = cacheService.getAccessToken(accessTokenKey);

    if (CommonUtils.isTokenExpired(accessToken)) {
      if (CommonUtils.isTokenExpired(refreshToken)) {
        Token token =
            domainService.companyLogin(
                ctx, clientId, clientCredential, companyAppId, companyAppCredential);
        cacheService.putAccessToken(accessTokenKey, token.getAccessToken());
        cacheService.putRefreshToken(refreshTokenKey, token.getRefreshToken());
        accessToken = token.getAccessToken();
      } else {
        Token token = domainService.refreshToken(ctx, refreshToken, clientId, clientCredential);
        cacheService.putAccessToken(accessTokenKey, token.getAccessToken());
        cacheService.putRefreshToken(refreshTokenKey, token.getRefreshToken());
        accessToken = token.getAccessToken();
      }
    }

    return accessToken;
  }
}
