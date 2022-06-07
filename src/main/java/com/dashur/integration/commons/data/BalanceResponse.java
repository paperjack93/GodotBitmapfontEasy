package com.dashur.integration.commons.data;

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
@EqualsAndHashCode(callSuper = false)
public class BalanceResponse {

  private String token;

  private String accountId;

  private String userId;

  private String currency;

  private BigDecimal balance;
}
