package com.dashur.integration.commons.data;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TransactionResponse extends BalanceResponse {

  private String txId;

  private BigDecimal amount;

  private String category;

  private String subCategory;

  private Boolean duplicate;

  public TransactionResponse() {
    this.duplicate = Boolean.FALSE;
  }
}
