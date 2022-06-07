package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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
public class TransactionModel {
  @JsonProperty("meta")
  private MetaModel meta;

  @JsonProperty("data")
  private List<DataModel> datas;

  @Data
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class DataModel {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("reserve_id")
    private Long reserveId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("application_id")
    private Long applicationId;

    @JsonProperty("currency_unit")
    private String currencyUnit;

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("wallet_code")
    private String walletCode;

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

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("reserve_balance")
    private BigDecimal reserveBalance;

    @JsonProperty("meta_data")
    private Map<String, Object> metaData;
  }
}
