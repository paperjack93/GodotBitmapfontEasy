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
public class DepositResponse extends Response {
  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("referenceId")
  private String referenceId;
}
