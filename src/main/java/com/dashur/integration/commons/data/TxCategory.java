package com.dashur.integration.commons.data;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import lombok.Getter;

@Getter
public enum TxCategory {
  WAGER("WAGER"),
  PAYOUT("PAYOUT"),
  REFUND("REFUND"),
  ENDROUND("ENDROUND");

  private String code;

  TxCategory(String code) {
    this.code = code;
  }

  public TxCategory resolveByCode(String code) {
    if (CommonUtils.isEmptyOrNull(code)) {
      throw new ValidationException(
          "TxCategory.resolveByCode(code) => [%s] is null or empty", code);
    }

    try {
      return TxCategory.valueOf(code);
    } catch (IllegalArgumentException ex) {
      throw new ApplicationException(
          "TxCategory.resolveByCode(code) => [%s] is not recognize", code);
    }
  }
}
