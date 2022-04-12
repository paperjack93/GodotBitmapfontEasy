package com.dashur.integration.extw.connectors.qt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AuthBonus {
  @JsonProperty("type")
  private String type;

  @JsonProperty("promoCode")
  private String promoCode;

  @JsonProperty("validityInDays")
  private Integer validityInDays;

  @JsonProperty("betAmountPerRound")
  private BigDecimal betAmountPerRound;

  @JsonProperty("numberOfRounds")
  private Integer numberOfRounds;

  @JsonProperty("rejectable")
  private Boolean rejectable;
}
