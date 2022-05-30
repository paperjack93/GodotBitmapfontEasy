package com.dashur.integration.extw.connectors.relaxgaming.data;

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
public class FinalizeRoundRequest extends Request {

  @JsonProperty("roundid")
  private String roundId;

  @JsonProperty("finalizedstatus")
  private String finalizedStatus;

  @JsonProperty("sessionid")
  private Long sessionId;

  // required when finalizedstatus=FINALIZED, omitted when NOROUND
  @JsonProperty("depositdata")
  private DepositRequest depositData;

}