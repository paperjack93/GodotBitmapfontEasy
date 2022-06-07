package com.dashur.integration.commons.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Domain object for account balance */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class AccountBalance {
  // user currency, ISO currency codes.
  private String currency;
  // user balance, in set to 2 decimal place.
  private BigDecimal balance;
}
