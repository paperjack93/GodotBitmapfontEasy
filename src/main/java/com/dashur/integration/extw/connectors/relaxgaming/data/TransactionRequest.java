package com.dashur.integration.extw.connectors.relaxgaming.data;

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

  @JsonProperty("playerid")
  private Integer playerId;

  @JsonProperty("roundid")
  private String roundId;   // 26 characters or less for some operators

  @JsonProperty("gameref")
  private String gameRef;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("clientid")
  private String clientId;  // Example: mobile-android

  @JsonProperty("txid")
  private String txId;

  @JsonProperty("sessionid")
  private Long sessionId;

  @JsonProperty("amount")
  private Long amount;      // In cents

  @JsonProperty("txtype")
  private String txType;

  @JsonProperty("ended")
  private Boolean ended;
}