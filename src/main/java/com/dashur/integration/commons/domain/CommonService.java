package com.dashur.integration.commons.domain;

import com.dashur.integration.commons.RequestContext;

/** Commonly used services that will shared for underlying integration implementation. */
public interface CommonService {
  /**
   * @param ctx in-coming request context
   * @param clientId client id used to refresh token
   * @param clientCredentials client credententials used to refresh token
   * @param playerToken in-coming token.
   * @param isTestUser validate whether is a test user or not
   * @return
   */
  RequestContext populateCtx(
      RequestContext ctx,
      String clientId,
      String clientCredentials,
      String playerToken,
      IsTestUser isTestUser);

  /**
   * @param ctx
   * @param appId
   * @param appCredentials
   * @param clientId
   * @param clientCredentials
   * @return
   */
  RequestContext refreshCtxOrForceRefreshToken(
      RequestContext ctx,
      String appId,
      String appCredentials,
      String clientId,
      String clientCredentials);

  /**
   * @param ctx in-coming request context
   * @param clientAppId
   * @param clientAppCredential
   * @return
   */
  String clientAppAccessToken(RequestContext ctx, String clientAppId, String clientAppCredential);

  /**
   * @param ctx in-coming request context
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @return
   */
  String companyAppAccessToken(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential);

  /** Interface to validate is test user. */
  interface IsTestUser {
    /**
     * @param username
     * @return
     */
    Boolean isTestUser(String username);
  }
}
