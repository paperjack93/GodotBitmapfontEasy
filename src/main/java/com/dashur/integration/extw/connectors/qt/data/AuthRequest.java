package com.dashur.integration.extw.connectors.qt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AuthRequest extends Request {
  @JsonProperty("gameId")
  private String gameId;

  @JsonProperty("ipAddress")
  private String ipAddress;
}
