package com.dashur.integration.commons.auth;

import lombok.Data;
import lombok.ToString;

/** Pair of refresh token and access token get from dashur auth-service. */
@Data
@ToString
public class Token {
  private String accessToken;
  private String refreshToken;

  public Token() {
    // default constructor
  }

  /**
   * @param accessToken
   * @param refreshToken
   */
  public Token(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
