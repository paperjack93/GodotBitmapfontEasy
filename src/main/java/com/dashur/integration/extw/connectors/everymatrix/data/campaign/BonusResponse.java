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
public class BonusResponse {
  @JsonProperty("Success")
  private Boolean success;

  @JsonProperty("Message")
  private String message;

  @JsonProperty("ErrorCode")
  private Integer errorCode;

  @JsonProperty("VendorBonusId")
  private String vendorBonusId;
}
