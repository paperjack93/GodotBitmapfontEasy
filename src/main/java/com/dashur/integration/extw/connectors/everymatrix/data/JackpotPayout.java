package com.dashur.integration.extw.connectors.everymatrix.data;

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
public class JackpotPayout {
  @JsonProperty("JackpotId")
  private String jackpotId;

  @JsonProperty("JackpotPayoutAmount")
  private BigDecimal jackpotPayoutAmount;
}
