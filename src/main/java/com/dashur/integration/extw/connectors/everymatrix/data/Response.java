package com.dashur.integration.extw.connectors.everymatrix.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Response {
  @JsonProperty("Status")
  private String status;

  @JsonProperty("ErrorCode")
  private Integer errorCode;

  @JsonProperty("ErrorDescription")
  private String errorDescription;

  @JsonProperty("LogId")
  private String logId;
}
