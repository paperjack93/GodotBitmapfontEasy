package com.dashur.integration.commons.auth;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.utils.CommonUtils;
import lombok.Getter;

@Getter
public enum GrantType {
  AUTHORIZATION_CODE(Constant.REST_AUTH_GRANT_TYPE_AUTHORIZATION_CODE),
  PASSWORD(Constant.REST_AUTH_GRANT_TYPE_PASSWORD),
  CLIENT_CREDENTIALS(Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS),
  REFRESH_TOKEN(Constant.REST_AUTH_GRANT_TYPE_REFRESH_TOKEN);

  private String code;

  GrantType(String code) {
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
  public static GrantType resolve(String code) {
    if (CommonUtils.isWhitespaceOrNull(code)) {
      throw new AuthException(
          AuthException.SubCode.ILLEGAL_ARGUMENT,
          "GrantType.resolve(code) => code is null or empty");
    }

    switch (code) {
      case Constant.REST_AUTH_GRANT_TYPE_AUTHORIZATION_CODE:
        return AUTHORIZATION_CODE;
      case Constant.REST_AUTH_GRANT_TYPE_PASSWORD:
        return PASSWORD;
      case Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS:
        return CLIENT_CREDENTIALS;
      case Constant.REST_AUTH_GRANT_TYPE_REFRESH_TOKEN:
        return REFRESH_TOKEN;
      default:
        throw new AuthException(
            AuthException.SubCode.ILLEGAL_ARGUMENT,
            "GrantType.resolve(code) => [%s] is not recognized",
            code);
    }
  }
}
