package com.dashur.integration.commons.data;

import lombok.Getter;

@Getter
public enum SubTxCategory {
  FREE("FREE"),
  POOL("POOL"),
  REWARD("REWARD"),
  TIP("TIP"),
  NONE(null);

  private String code;

  SubTxCategory(String code) {
    this.code = code;
  }
}
