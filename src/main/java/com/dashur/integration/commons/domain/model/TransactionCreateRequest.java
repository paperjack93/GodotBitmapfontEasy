package com.dashur.integration.commons.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain object for transaction create request. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateRequest {
  private Long accountId;
  private String category;
  private String subCategory;
  private Money amount;
  private String externalRef;
  private String extItemId;
  private Metadata metadata;

  private Long reserveId;
  private String reserveExpiryTime;

  // Common constructor for transaction without Reserve / Release
  public TransactionCreateRequest(
      Long accountId,
      String category,
      String subCategory,
      Money amount,
      String externalRef,
      String extItemId,
      Metadata metadata) {
    this.accountId = accountId;
    this.category = category;
    this.subCategory = subCategory;
    this.amount = amount;
    this.externalRef = externalRef;
    this.extItemId = extItemId;
    this.metadata = metadata;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class Money {
    private String currency;
    private BigDecimal amount;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class Metadata {
    private String roundId;
    private Map<String, Object> vendor;
  }
}
