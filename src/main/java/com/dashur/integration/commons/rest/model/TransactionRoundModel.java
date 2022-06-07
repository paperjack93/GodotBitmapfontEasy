package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.*;
import lombok.experimental.Wither;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(builderMethodName = "newTransactionRoundBuilder")
@Wither
@EqualsAndHashCode(callSuper = false)
public class TransactionRoundModel {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("account_id")
  private Long accountId;

  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  @JsonProperty("application_id")
  private Long applicationId;

  @JsonProperty("wallet_code")
  private String walletCode;

  @JsonProperty("currency_unit")
  private String currencyUnit;

  @JsonProperty("external_ref")
  private String externalRef;

  @JsonProperty("status")
  private String status;

  @JsonProperty("transaction_ids")
  private List<Long> transactionIds;

  @JsonProperty("num_of_wager")
  private Integer numOfWager;

  @JsonProperty("sum_of_wager")
  private BigDecimal sumOfWager;

  @JsonProperty("num_of_payout")
  private Integer numOfPayout;

  @JsonProperty("sum_of_payout")
  private BigDecimal sumOfPayout;

  @JsonProperty("num_of_refund")
  private Integer numOfRefund;

  @JsonProperty("sum_of_refund")
  private BigDecimal sumOfRefund;

  @JsonProperty("sum_of_refund_credit")
  private BigDecimal sumOfRefundCredit;

  @JsonProperty("sum_of_refund_debit")
  private BigDecimal sumOfRefundDebit;

  @JsonProperty("start_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date startTime;

  @JsonProperty("close_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date closeTime;

  @JsonProperty("meta_data")
  private Map<String, Object> metaData;

  @JsonProperty("start_balance")
  private BigDecimal startBalance;

  @JsonProperty("last_balance")
  private BigDecimal lastBalance;

  @JsonProperty("close_balance")
  private BigDecimal closeBalance;
}
