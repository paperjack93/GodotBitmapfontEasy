package com.dashur.integration.commons.auth;

import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** mapped jwt string to RefreshToken */
@Getter
@AllArgsConstructor
@Slf4j
public class RefreshToken {
  private static final String TOKEN_SEP = ".";
  private static final String TOKEN_SEP_REGEX = "\\.";
  private static final int MIN_TOKEN_ELEMENTS = 3;

  private static final String KEY_ACCOUNT_ID = "aid";
  private static final String KEY_USER_ID = "uid";
  private static final String KEY_USERNAME = "user_name";
  private static final String KEY_EXTERNAL_WALLET = "extw";
  private static final String KEY_ACCOUNT_PATH = "ap";
  private static final String KEY_APPLICATION_ID = "pid";
  private static final String KEY_TENANT_ID = "tid";
  private static final String KEY_LINKED_TENANT_ID = "ltid";

  private long accountId;
  private long userId;
  private String username;
  private boolean externalWallet;
  private String accountPath;
  private long applicationId;
  private long tenantId;
  private long linkedTenantId;

  /**
   * parsed string to RefreshToken
   *
   * @param refreshTokenStr
   * @return
   */
  public static RefreshToken parse(String refreshTokenStr) {
    HashToken.CheckStatus tokenCheckStatus = checkRefreshToken(refreshTokenStr);
    if (!tokenCheckStatus.isValid()) {
      if (log.isDebugEnabled()) {
        log.debug("refresh-token is invalid => [{}]", refreshTokenStr);
      }
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_TOKEN,
          "RefreshToken.parse(refreshTokenStr) => [%s] is invalid",
          refreshTokenStr);
    }

    try {
      String[] elements = tokenCheckStatus.getElements();
      String payloadEncoded = elements[1];
      String payload = CommonUtils.base64Decode(payloadEncoded);

      Map<String, Object> userInfo = CommonUtils.jsonReadMap(payload);
      long accountId = Long.parseLong(userInfo.get(KEY_ACCOUNT_ID).toString());
      long userId = Long.parseLong(userInfo.get(KEY_USER_ID).toString());
      String username = (String) userInfo.get(KEY_USERNAME);
      boolean externalWallet = false;
      if (userInfo.containsKey(KEY_EXTERNAL_WALLET)) {
        externalWallet = Boolean.valueOf(userInfo.get(KEY_EXTERNAL_WALLET).toString());
      }
      String accountPath = "";
      if (userInfo.containsKey(KEY_ACCOUNT_PATH)) {
        accountPath = (String) userInfo.get(KEY_ACCOUNT_PATH);
      }
      Long applicationId = 0L;
      if (userInfo.containsKey(KEY_APPLICATION_ID)) {
        applicationId = Long.parseLong(userInfo.get(KEY_APPLICATION_ID).toString());
      }
      Long tenantId = 0L;
      if (userInfo.containsKey(KEY_TENANT_ID)) {
        tenantId = Long.parseLong(userInfo.get(KEY_TENANT_ID).toString());
      }
      Long linkedTenantId = 0L;
      if (userInfo.containsKey(KEY_LINKED_TENANT_ID)) {
        linkedTenantId = Long.parseLong(userInfo.get(KEY_LINKED_TENANT_ID).toString());
      }
      return new RefreshToken(
          accountId,
          userId,
          username,
          externalWallet,
          accountPath,
          applicationId,
          tenantId,
          linkedTenantId);
    } catch (Exception e) {
      if (log.isDebugEnabled()) {
        log.debug("refresh-token is invalid => [{}]", refreshTokenStr, e);
      }
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_TOKEN,
          "RefreshToken.parse(refreshTokenStr) => [%s] is invalid",
          refreshTokenStr);
    }
  }

  /**
   * check status of the refreshtoken string.
   *
   * @param refreshTokenStr
   * @return
   */
  private static HashToken.CheckStatus checkRefreshToken(String refreshTokenStr) {
    if (CommonUtils.isWhitespaceOrNull(refreshTokenStr)) {
      return new HashToken.CheckStatus(Boolean.FALSE);
    }

    if (!refreshTokenStr.contains(TOKEN_SEP)) {
      return new HashToken.CheckStatus(Boolean.FALSE);
    }

    String[] fragments = refreshTokenStr.split(TOKEN_SEP_REGEX);
    return new HashToken.CheckStatus(fragments.length >= MIN_TOKEN_ELEMENTS, fragments);
  }
}
