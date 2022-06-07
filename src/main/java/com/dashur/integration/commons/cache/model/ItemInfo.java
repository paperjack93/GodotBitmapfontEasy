package com.dashur.integration.commons.cache.model;

import com.dashur.integration.commons.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ItemInfo {
  @JsonProperty("item_id")
  private Long itemId;

  @JsonProperty("ext_ref")
  private String extRef;

  @JsonProperty("tenant_id")
  private Long tenantId;

  @JsonProperty("campaign_owner_id")
  private Long campaignOwnerId;

  @JsonProperty("linked_item_id")
  private Long linkedItemId;

  @JsonProperty("linked_ext_ref")
  private String linkedExtRef;

  @JsonProperty("linked_tenant_id")
  private Long linkedTenantId;

  @JsonProperty("linked_campaign_owner_id")
  private Long linkedCampaignOwnerId;

  /** @return if its linked item, return the linked-ext-ref else return the ext-ref */
  public String resolvedExtRef() {
    if (CommonUtils.isEmptyOrNull(linkedExtRef)) {
      return extRef;
    }

    return linkedExtRef;
  }

  /**
   * return resolved owner id.
   *
   * @return
   */
  public Long resolveOwnerId() {
    if (Objects.isNull(linkedCampaignOwnerId) || 0L == linkedCampaignOwnerId.longValue()) {
      return campaignOwnerId;
    }

    return linkedCampaignOwnerId;
  }

  /** @return true if linkedExtRef exists. */
  public Boolean isLinkedItem() {
    if (CommonUtils.isEmptyOrNull(linkedExtRef)) {
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }
}
