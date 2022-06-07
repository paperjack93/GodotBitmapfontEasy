package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampaignModelExt extends CampaignModel {

  @JsonProperty("launch_id")
  private String launchId;

  @JsonProperty("game_ext_ref")
  private String gameExtRef;

  @JsonProperty("tenant_id")
  private Long tenantId;

  @JsonProperty("vendor_id")
  private Long vendorId;

  /*
   * For multi games campaign to work, we need a vendor_ref that includes game_id to ensure uniqueness.
   * Format should be 'campaign_id::campaign_ext_ref(::game_id)'
   *
   * For voucher campaign type, we will use extra prefix to differentiate.
   * Format should be 'V::campaign_id::campaign_ext_ref(::game_id)'
   *
   * Example:
   * 1. 123::any-reference-string
   * 2. 123::3d9fa55f-8fa3-473c-ae46-43ff434fb176::1392 => for multi games (additional 'game_id' suffix)
   * 2. V::123::3d9fa55f-8fa3-473c-ae46-43ff434fb176 => for voucher claim (additional 'V::' prefix)
   */
  @JsonIgnore
  public String getCampaignRef() {
    boolean multiGames = Objects.nonNull(this.getGames()) && this.getGames().size() > 1;
    String campaignRef = String.format("%s::%s", this.getId(), this.getExtRef());

    if (multiGames) {
      campaignRef = String.format("%s::%s", campaignRef, this.getGameId());
    }

    if (this.getType().equals(Type.VOUCHER_GAMES)) {
      campaignRef = "V::".concat(campaignRef);
    }

    return campaignRef;
  }
}
