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
public class DasAuthResponse extends DasResponse {
  @JsonProperty("username")
  private String username;

  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  @JsonProperty("balance")
  private DasMoney balance;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("country")
  private String country;

  @JsonProperty("lang")
  private String lang;
}
