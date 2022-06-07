package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionFeedModel {
  @JsonProperty("test")
  private Boolean test;

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

  @JsonProperty("sum_of_refund_credit")
  private BigDecimal sumOfRefundCredit;

  @JsonProperty("sum_of_refund_debit")
  private BigDecimal sumOfRefundDebit;

  @JsonProperty("revenue")
  private BigDecimal revenue;

  @JsonProperty("transaction_ids")
  private List<Long> transactionIds;

  @JsonProperty("wallet_code")
  private String walletCode;

  @JsonProperty("external_ref")
  private String externalRef;

  @JsonProperty("category")
  private String category;

  @JsonProperty("sub_category")
  private String subCategory;

  @JsonProperty("balance_type")
  private String balanceType;

  @JsonProperty("type")
  private String type;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("meta_data")
  private Map<String, Object> metaData;

  @JsonProperty("id")
  private Long id;

  @JsonProperty("parent_transaction_id")
  private Long parentTransactionId;

  @JsonProperty("account_id")
  private Long accountId;

  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  @JsonProperty("application_id")
  private Long applicationId;

  @JsonProperty("currency_unit")
  private String currencyUnit;

  @JsonProperty("transaction_time")
  private String transactionTime;

  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("loyalty_balance")
  private BigDecimal loyaltyBalance;

  @JsonProperty("pool_amount")
  private BigDecimal poolAmount;

  @JsonProperty("loyalty_amount")
  private BigDecimal loyaltyAmount;

  @JsonProperty("created_by")
  private Long createdBy;

  @JsonProperty("created")
  private String created;

  @JsonProperty("session")
  private String session;

  @JsonProperty("ip")
  private InetAddress ip;
}
