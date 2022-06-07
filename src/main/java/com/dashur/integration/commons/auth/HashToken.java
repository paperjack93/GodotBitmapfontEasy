package com.dashur.integration.commons.auth;

import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.commons.utils.EncryptionUtils;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

@Getter
@Wither
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class HashToken {
  private static final String TOKEN_SEP = "::";
  private static final String TOKEN_SEP_REGEX = "::";
  private static final int MIN_TOKEN_ELEMENTS = 9;

  // hash of username
  private String hashOfUsername;

  // platformId -> e.g. mobile, desktop, flash, html5, any games identifier
  // different integrator has different name for this, for e.g.
  // MG integration call it clientTypeId
  // PNG call it channelId
  private String platform;

  // running number to make each hash token appear to be different.
  private Integer index;

  // dashur account id
  private Long accountId;

  // dashur user id
  private Long userId;

  // dashur username
  private String username;

  // whether linked to an external wallet
  private Boolean externalWallet;

  // dash account path
  private String accountPath;

  // TODO: for language, we should make use of the integer to language mapped, those languages that
  // we don't supported in translation should be mapped to en instead. to safe space in the lenght
  // of hash token.
  // locale
  private Locale language;

  /**
   * Parsed hash-token-string to hash token.
   *
   * @param hashTokenStr
   * @return
   */
  public static HashToken parse(String hashTokenStr) {
    CheckStatus tokenCheckStatus = checkHashToken(hashTokenStr);
    if (!tokenCheckStatus.isValid()) {
      if (log.isDebugEnabled()) {
        log.debug("hash-token is invalid => [{}]", hashTokenStr);
      }
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_TOKEN,
          "HashToken.parse(hashTokenStr) => [%s] is invalid",
          hashTokenStr);
    }

    try {
      String[] elements = tokenCheckStatus.elements;
      String hashOfUsername = elements[0];
      String platformId = elements[1];
      Integer index = Integer.parseInt(elements[2]);
      Long accountId = Long.parseLong(elements[3]);
      Long userId = Long.parseLong(elements[4]);
      String username = elements[5];
      Boolean externalWallet = Integer.valueOf(elements[6]) == 1;
      String accountPath = elements[7];
      Locale language = CommonUtils.parseLocale(elements[8]);
      return new HashToken(
          hashOfUsername,
          platformId,
          index,
          accountId,
          userId,
          username,
          externalWallet,
          accountPath,
          language);
    } catch (Exception e) {
      if (log.isDebugEnabled()) {
        log.debug("hash-token is invalid => [{}]", hashTokenStr);
      }
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_TOKEN,
          "HashToken.parse(hashTokenStr) => [%s] is invalid",
          hashTokenStr);
    }
  }

  /**
   * parsed refresh token to hash token
   *
   * @param platform
   * @param locale
   * @param refreshToken
   * @return
   */
  public static HashToken fromRefreshToken(String platform, Locale locale, String refreshToken) {
    RefreshToken parsedRefreshToken = RefreshToken.parse(refreshToken);
    return fromRefreshToken(platform, locale, parsedRefreshToken);
  }

  /**
   * parsed refresh token to hash token
   *
   * @param platform
   * @param locale
   * @param refreshToken
   * @return
   */
  public static HashToken fromRefreshToken(
      String platform, Locale locale, RefreshToken refreshToken) {
    String username = refreshToken.getUsername();
    String hashOfUsername = EncryptionUtils.secureHash(username);
    long accountId = refreshToken.getAccountId();
    long userId = refreshToken.getUserId();
    int index = 1;
    boolean isExternalWallet = refreshToken.isExternalWallet();
    String accountPath = refreshToken.getAccountPath();
    HashToken hashToken =
        new HashToken(
            hashOfUsername,
            platform,
            index,
            accountId,
            userId,
            username,
            isExternalWallet,
            accountPath,
            locale);
    return hashToken;
  }

  /** Simple method to check if the passing string is in a correct hash token format. */
  public static boolean isHashToken(String hashTokenStr) {
    return checkHashToken(hashTokenStr).valid;
  }

  /**
   * internally used check hash token statuses. ignoreable.
   *
   * @param hashTokenStr
   * @return
   */
  private static CheckStatus checkHashToken(String hashTokenStr) {
    if (CommonUtils.isWhitespaceOrNull(hashTokenStr)) {
      return new CheckStatus(Boolean.FALSE);
    }

    if (!hashTokenStr.contains(TOKEN_SEP)) {
      return new CheckStatus(Boolean.FALSE);
    }

    String[] fragments = hashTokenStr.split(TOKEN_SEP);
    return new CheckStatus(fragments.length >= MIN_TOKEN_ELEMENTS, fragments);
  }

  public String getCacheKey() {
    return hashOfUsername + "." + platform;
  }

  @Override
  public String toString() {
    return String.format(
        "%s::%s::%d::%d::%d::%s::%d::%s::%s",
        hashOfUsername,
        platform,
        index,
        accountId,
        userId,
        username,
        (externalWallet ? 1 : 0),
        accountPath,
        language);
  }

  /** Simple tuple wrapper, to optimise some code, ignoreable. */
  @Data
  @AllArgsConstructor
  static final class CheckStatus {
    private final boolean valid;
    private final String[] elements;

    CheckStatus(Boolean valid) {
      this.valid = valid;
      this.elements = new String[] {};
    }
  }
}
