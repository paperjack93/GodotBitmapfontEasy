package com.dashur.integration.extw.connectors.relaxgaming.data.service;

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
// only from PokerStars
public class FinalizeRequest extends ServiceRequest {

  // optional

  @JsonProperty("roundid")
  private String roundId;

  @JsonProperty("partnerid")
  private Integer partnerId;

  @JsonProperty("sessionid")
  private Long sessionId;
  
}
