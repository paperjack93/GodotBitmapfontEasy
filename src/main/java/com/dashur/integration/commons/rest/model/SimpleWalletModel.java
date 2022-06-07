package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Simplified wallet model, doesn't need all attributes. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleWalletModel {
  /** The type of the Wallet. Eg. CREDIT or CASH. */
  @JsonProperty("type")
  private String type;

  /** The system unique id for the Wallet. */
  @JsonProperty("id")
  private Long id;

  /** The currency of the wallet */
  @JsonProperty("currency_unit")
  private String currencyUnit;

  /**
   * *********************************** Properties from WalletBaseModel
   * ***********************************
   */
  /** The id of the Account this Wallet belongs to. */
  @JsonProperty("account_id")
  private Long accountId;

  /** The code of the Wallet. */
  @JsonProperty("code")
  private String code;

  /** The name of the Wallet. */
  @JsonProperty("name")
  private String name;

  /** The id of Tenant allow to link */
  @JsonProperty("linked_tenant_id")
  private Long linkedTenantId;
}
