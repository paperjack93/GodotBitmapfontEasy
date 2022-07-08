package com.dashur.integration.extw.connectors.relaxgaming.data.service;

import java.util.List;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class GetReplayResponse {

  @JsonProperty("replayurl")
  private String replayUrl;

  @JsonProperty("replay")
  private String replayImage;

  @JsonProperty("imageformat")
  private String imageFormat;   // "jpeg" or "png"

  @JsonProperty("roundstart")
  private ZonedDateTime roundStart;

  @JsonProperty("roundend")
  private ZonedDateTime roundEnd;

  @JsonProperty("betamount")
  private Long betAmount;       // In cents

  @JsonProperty("winamount")
  private Long winAmount;       // In cents

  @JsonProperty("currency")
  private String currency;

}
