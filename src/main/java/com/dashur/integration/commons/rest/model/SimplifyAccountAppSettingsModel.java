package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifyAccountAppSettingsModel {
  /** The id of the Account App Settings */
  @JsonProperty("id")
  private Long id;

  /** The id of the Application which the App Settings links to */
  @NotNull
  @JsonProperty("app_id")
  private Long appId;

  /** This is a JSON map of settings. { "tags" : ["cool", "wicked"] } */
  @JsonProperty("settings")
  private Map<String, Object> settings;
}
