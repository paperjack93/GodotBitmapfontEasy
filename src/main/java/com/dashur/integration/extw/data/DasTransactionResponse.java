package com.dashur.integration.extw.data;

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
public class DasTransactionResponse extends DasResponse {
  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("ext_tx_id")
  private String extTxId;
}
