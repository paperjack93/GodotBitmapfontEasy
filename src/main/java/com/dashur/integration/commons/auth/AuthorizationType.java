package com.dashur.integration.commons.auth;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.utils.CommonUtils;
import lombok.Getter;

@Getter
public enum AuthorizationType {
  BASIC(Constant.REST_AUTH_AUTHORIZATION_TYPE_BASIC),
  BEARER(Constant.REST_AUTH_AUTHORIZATION_TYPE_BEARER),
  DIGEST(Constant.REST_AUTH_AUTHORIZATION_TYPE_DIGEST),
  HOBA(Constant.REST_AUTH_AUTHORIZATION_TYPE_HOBA),
  MUTUAL(Constant.REST_AUTH_AUTHORIZATION_TYPE_MUTUAL),
  AWS4_HMAC_SHA256(Constant.REST_AUTH_AUTHORIZATION_TYPE_AWS4_HMAC_SHA256);

  private String code;

  AuthorizationType(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return this.code;
  }

  /**
   * get GrantType from code
   *
   * @param code
   * @return
   */
  public static AuthorizationType resolve(String code) {
    if (CommonUtils.isWhitespaceOrNull(code)) {
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_ARGUMENT,
          "AuthorizationType.resolve(code) => code is null or empty");
    }

    switch (code) {
      case Constant.REST_AUTH_AUTHORIZATION_TYPE_BASIC:
        return BASIC;
      case Constant.REST_AUTH_AUTHORIZATION_TYPE_BEARER:
        return BEARER;
      case Constant.REST_AUTH_AUTHORIZATION_TYPE_DIGEST:
        return DIGEST;
      case Constant.REST_AUTH_AUTHORIZATION_TYPE_HOBA:
        return HOBA;
      case Constant.REST_AUTH_AUTHORIZATION_TYPE_MUTUAL:
        return MUTUAL;
      case Constant.REST_AUTH_AUTHORIZATION_TYPE_AWS4_HMAC_SHA256:
        return AWS4_HMAC_SHA256;
      default:
        throw new AuthException(
            AuthException.SubCode.ILLEGAL_ARGUMENT,
            "AuthorizationType.resolve(code) => [%s] is not recognize",
            code);
    }
  }
}
