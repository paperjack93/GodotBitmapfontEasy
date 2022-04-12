package com.dashur.integration.extw.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DasTransactionRequest extends DasRequest {
  @JsonProperty("currency")
  private String currency;

  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  @JsonProperty("category")
  private DasTransactionCategory category;

  @JsonProperty("tx_id")
  private long txId;

  @JsonProperty("refund_tx_id")
  private long refundTxId;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("pool_amount")
  private BigDecimal poolAmount;

  @JsonProperty("item_id")
  private long itemId;

  @JsonProperty("application_id")
  private long applicationId;

  @JsonProperty("round_id")
  private String roundId;

  @JsonProperty("campaign_id")
  private Long campaignId;

  @JsonProperty("campaign_ext_ref")
  private String campaignExtRef;
}
