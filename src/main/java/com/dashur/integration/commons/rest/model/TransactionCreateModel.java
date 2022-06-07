package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionCreateModel {
  @JsonProperty("account_id")
  private Long accountId;

  @JsonProperty("category")
  private String category;

  @JsonProperty("sub_category")
  private String subCategory;

  @JsonProperty("amount")
  private TransactionCreateModel.MoneyModel amount;

  @JsonProperty("external_ref")
  private String externalRef;

  @JsonProperty("ext_item_id")
  private String extItemId;

  @JsonProperty("meta_data")
  private TransactionCreateModel.MetadataModel metadata;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("reserve_id")
  private Long reserveId;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("reserve_expiry_time")
  private String reserveExpiryTime;

  @Data
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class MoneyModel {
    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount")
    private BigDecimal amount;
  }

  @Data
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class MetadataModel {
    @JsonProperty("round_id")
    private String roundId;

    @JsonProperty("vendor")
    private Map<String, Object> vendorSpecificMetaData;
  }
}
