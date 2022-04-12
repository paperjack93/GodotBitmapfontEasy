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
public class PromoRequest extends Request {

  @JsonProperty("playerId")
  private String playerId;

  @JsonProperty("txId")
  private String txId;

  @JsonProperty("amount")
  private Integer amount; // In cents

  public PromoRequest(WinRequest request) {
    this.setCasinoId(request.getCasinoId());
    this.playerId = request.getPlayerId();
    this.txId = request.getTxId();
    this.amount = request.getAmount();
  }
}
