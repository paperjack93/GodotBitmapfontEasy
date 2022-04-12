package com.dashur.integration.extw.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DasBalanceRequest extends DasRequest {
  @JsonProperty("currency")
  private String currency;

  @JsonProperty("account_ext_ref")
  private String accountExtRef;
}
