package com.dashur.integration.extw.connectors.parimatch.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class TransactionRequest extends Request {

  @JsonProperty("playerId")
  private String playerId;

  @JsonProperty("productId")
  private String gameId;

  @JsonProperty("txId")
  private String txId;

  @JsonProperty("roundId")
  private String roundId;

  @JsonProperty("amount")
  private Integer amount; // In cents

  @JsonProperty("currency")
  private String currency;
}
