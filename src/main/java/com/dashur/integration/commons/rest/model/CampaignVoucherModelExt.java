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
public class CampaignVoucherModelExt extends CampaignVoucherModel {

  @JsonProperty("member")
  private CampaignMemberModelExt member;

  @JsonIgnore
  @Override
  public Long getAccountId() {
    return this.member.getAccountId();
  }

  @JsonIgnore
  @Override
  public String getAccountExtRef() {
    return this.member.getAccountExtRef();
  }

  @JsonIgnore
  public CampaignModelExt getCampaign() {
    return this.member.getCampaign();
  }

  @JsonIgnore
  public String getCampaignRef() {
    return this.member.getCampaignRef();
  }
}
