package com.dashur.integration.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyUtils {
  /**
   * Utility to ensure fractional digits
   *
   * @param currency The currency code
   * @param amount The amount
   * @return The scaled amount
   */
  public static BigDecimal ensureAmountScaleByCurrency(String currency, BigDecimal amount) {
    CurrencyUnit currencyUnit =
        Monetary.getCurrency(currency, "com.dashur.integration.commons.currency.CurrencyProvider");

    if (Objects.nonNull(amount) && Objects.nonNull(currencyUnit)) {
      amount = amount.stripTrailingZeros();

      if (amount.scale() > currencyUnit.getDefaultFractionDigits()) {
        log.info(
            "Amount : [{}] exceeded currency [{}] scale of [{}]",
            amount,
            currencyUnit.getCurrencyCode(),
            currencyUnit.getDefaultFractionDigits());
        amount = amount.setScale(currencyUnit.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
      }
    }

    return amount;
  }
}
