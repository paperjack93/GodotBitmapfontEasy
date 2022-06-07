package com.dashur.integration.commons.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionFeedResponse extends BalanceResponse {

  private String txId;

  private String extRef;

  private BigDecimal amount;

  private String category;

  private String subCategory;

  private Integer numOfWager;

  private Integer numOfPayout;

  private Integer numOfRefund;

  private Map<String, Object> metaData;

  @JsonIgnore
  public String getExtItemId() {
    return this.metaData.getOrDefault("ext_item_id", "").toString();
  }
}
