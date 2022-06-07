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
public class SimplifyApplicationModel {
  /** The id of the Application */
  @JsonProperty("id")
  private Long id;

  /** The system unique id for the Vendor owns this application. */
  @JsonProperty("vendor_id")
  private Long vendorId;

  /** The id of the Account who owns this Application */
  @JsonProperty("owner_id")
  private Long ownerId;

  /** The name of the Application. */
  @JsonProperty("name")
  private String name;

  /** if app is linked */
  @JsonProperty("linked")
  private Boolean linked;

  /** app id linked in other tenant */
  @JsonProperty("linked_app_id")
  private Long linkedAppId;

  /** company id linked in other tenant */
  @JsonProperty("linked_company_id")
  private Long linkedCompanyId;

  /** tenant id linked in other tenant */
  @JsonProperty("linked_tenant_id")
  private Long linkedTenantId;
}
