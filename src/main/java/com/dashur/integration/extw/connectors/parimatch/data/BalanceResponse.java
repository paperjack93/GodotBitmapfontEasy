package com.dashur.integration.extw.connectors.parimatch.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class BalanceResponse extends Response {

  @JsonProperty("playerId")
  private String playerId;

  @JsonProperty("displayName")
  private String playerName;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("country")
  private String country;
}
