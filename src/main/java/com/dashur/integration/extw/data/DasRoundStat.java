package com.dashur.integration.extw.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DasRoundStat {
  @JsonProperty("category")
  private DasTransactionCategory category;

  @JsonProperty("count")
  private int count;

  @JsonProperty("sum")
  private BigDecimal sum;

  @JsonProperty("sum_credit")
  private BigDecimal sumCredit;

  @JsonProperty("sum_debit")
  private BigDecimal sumDebit;
}
