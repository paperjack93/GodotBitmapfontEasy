package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifyLauncherItemModel {
  @JsonProperty("token")
  private String token;

  @JsonProperty("ext_ref")
  private String extRef;

  @JsonProperty("app_id")
  private Long appId;

  @JsonProperty("external")
  private Boolean external;

  @JsonProperty("item_id")
  private Long itemId;

  @JsonProperty("demo")
  private Boolean demo;

  @JsonProperty("conf_params")
  private Map<String, Object> confParams;

  @JsonProperty("login_context")
  private Map<String, Object> ctx;
}
