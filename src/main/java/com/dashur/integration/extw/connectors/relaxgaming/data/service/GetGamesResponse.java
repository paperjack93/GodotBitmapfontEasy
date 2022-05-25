package com.dashur.integration.extw.connectors.relaxgaming.data.service;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

// relax

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class GetGamesResponse {

  class FreeRoundsInfo {
    @JsonProperty("channels")
    private List<String> channels;

    @JsonProperty("featuretriggers")
    private FeatureTriggerInfo featuretriggers;
  }

  class FeatureTriggerInfo {
    @JsonProperty("channels")
    private List<String> channels;
  }

  class RngInfo {
    @JsonProperty("identification")
    private String identification;

    @JsonProperty("softwareid")
    private String softwareId;
  }

  @JsonProperty("gameref")
  private String gameRef;

  @JsonProperty("name")
  private String name;

  @JsonProperty("studio")
  private String studio;

  @JsonProperty("freespins")
  private FreeRoundsInfo freespins;

  @JsonProperty("legalbetsizes")
  private List<Integer> legalBetSizes;  // In euro cents

  // optional

  @JsonProperty("rng")
  private RngInfo rngInfo;

  @JsonProperty("category")
  private String category;

}
