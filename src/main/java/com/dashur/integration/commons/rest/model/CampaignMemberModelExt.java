package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampaignMemberModelExt extends CampaignMemberModel {

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("assign_ref")
  private String assignRef;

  @JsonProperty("campaign")
  private CampaignModelExt campaign;

  @JsonIgnore
  public String getCampaignRef() {
    return this.campaign.getCampaignRef();
  }
}
