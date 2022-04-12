package com.dashur.integration.extw.connectors.everymatrix.data.campaign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ForfeitBonusRequest {
  @JsonProperty("UserId")
  private String userId;

  @JsonProperty("BonusId")
  private String bonusId;
}
