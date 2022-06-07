package com.dashur.integration.commons.data;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionRoundResponse {

  private Long id;

  private Long accountId;

  private String currency;

  private String roundId;

  private TransactionRoundStatus status;

  private List<Long> transactionIds;

  private Integer numOfWager;

  private Integer numOfPayout;

  private Integer numOfRefund;

  private BigDecimal sumOfWager;

  private BigDecimal sumOfPayout;

  private BigDecimal sumOfRefundCredit;

  private BigDecimal sumOfRefundDebit;

  private BigDecimal startBalance;

  private BigDecimal lastBalance;

  private BigDecimal closeBalance;
}
