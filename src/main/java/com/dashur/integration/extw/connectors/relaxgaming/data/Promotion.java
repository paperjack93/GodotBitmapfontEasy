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
public class Promotion {

  // optional

  @JsonProperty("promotionstype")
  private String promotionType;

  @JsonProperty("promotionid")
  private Long promotionId;

  @JsonProperty("txid")
  private String txId;

  @JsonProperty("playerid")
  private Integer playerId;

  @JsonProperty("partnerid")
  private Integer partnerId;

  @JsonProperty("gameref")
  private String gameRef;

  @JsonProperty("amount")
  private Integer amount; // amount of rounds. valid range 1-5000

  @JsonProperty("freespinvalue")
  private Long freeSpinValue; // in cents

  // ISO 8601
  @JsonProperty("expires")
  private String expires;

  @JsonProperty("promocode")
  private String promoCode;

}
