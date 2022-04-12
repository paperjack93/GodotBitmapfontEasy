package com.dashur.integration.extw.connectors.qt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AuthResponse extends Response {
  @JsonProperty("playerId")
  private String playerId;

  @JsonProperty("screenName")
  private String screenName;

  @JsonProperty("language")
  private String language;

  @JsonProperty("country")
  private String country;

  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("jurisdiction")
  private String jurisdiction;

  @JsonProperty("maxBetAmount")
  private BigDecimal maxBetAmount;

  @JsonProperty("bonuses")
  private List<AuthBonus> bonuses;
}
