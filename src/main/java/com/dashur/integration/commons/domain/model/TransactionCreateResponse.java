package com.dashur.integration.commons.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain object for transaction create request. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateResponse {
  private Integer status;
}
