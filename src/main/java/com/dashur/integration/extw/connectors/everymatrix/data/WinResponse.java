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
public class WinResponse extends Response {
  @JsonProperty("TotalBalance")
  private BigDecimal totalBalance;

  @JsonProperty("Currency")
  private String currency;

  @JsonProperty("Status")
  private String status;
}
