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
public class AuthenticateRequest extends Request {
  @JsonProperty("LaunchToken")
  private String launchToken;

  @JsonProperty("RequestScope")
  private String requestScope;
}
