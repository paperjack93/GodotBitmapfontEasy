package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginMemberModel {
  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("username")
  private String username;

  @JsonProperty("token")
  private String token;

  @JsonProperty("short_token")
  private Boolean shortToken;

  @JsonProperty("scopes")
  private List<String> scopes;

  @JsonProperty("linking_app_id")
  private Long linkingAppId;

  @JsonProperty("validity_seconds")
  private Integer validitySeconds;
}
