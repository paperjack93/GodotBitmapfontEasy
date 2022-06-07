package com.dashur.integration.commons.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain object for transaction */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  private Long id;
  private Long accountId;
  private Long applicationId;
  private String currencyUnit;
  private String transactionTime;
  private String walletCode;
  private String category;
  private String subCategory;
  private String balanceType;
  private String type;
  private BigDecimal amount;
  private BigDecimal balance;
  private Map<String, Object> metaData;

  private Long reserveId;
  private BigDecimal reserveBalance;
}
