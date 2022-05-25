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
public class AddFreeSpinsRequest extends Request {

  @JsonProperty("txid")
  private String txId;

  @JsonProperty("playerid")
  private String playerId;

  @JsonProperty("gameref")
  private String gameRef;

  @JsonProperty("amount")
  private Long amount;          // number of free rounds. valid range 1-5000

  @JsonProperty("freespinsvalue")
  private Long freeSpinsValue;  // value of single round in cents. EUR if currency is empty

  // optional

  @JsonProperty("partnerid")
  private Integer partnerId;    // assigned by Relax Gaming. default 10 for the dev env

  @JsonProperty("expires")
  private String expires;       // ISO 8601. max 183 days

  @JsonProperty("currency")
  private String currency;      // ISO 4217

  @JsonProperty("promocode")
  private String promoCode;     // passed in deposit calls if set. max 64 characters

  @JsonProperty("playercurrency")
  private String playerCurrency;// ISO 4217. can be different from currency

  @JsonProperty("jurisdiction")
  private String jurisdiction;

}