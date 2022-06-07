package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberTokenModel {
  @JsonProperty("data")
  private TokenModel tokenData;

  @Data
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class TokenModel {
    @JsonProperty("token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
  }
}
