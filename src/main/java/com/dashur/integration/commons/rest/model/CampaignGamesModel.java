package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder(builderMethodName = "newCampaignGamesModelBuilder")
@Wither
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignGamesModel {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("level")
  private Integer level;
}
