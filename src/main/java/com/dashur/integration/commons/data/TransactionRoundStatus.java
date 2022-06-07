package com.dashur.integration.commons.data;

import lombok.Getter;

@Getter
public enum TransactionRoundStatus {
  UNKNOWN("UNKNOWN"),
  OPEN("OPEN"),
  CLOSED("CLOSED");

  private String code;

  TransactionRoundStatus(String code) {
    this.code = code;
  }
}
