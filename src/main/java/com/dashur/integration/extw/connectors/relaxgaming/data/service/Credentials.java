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
public class Credentials {

  @JsonProperty("partnerid")
  private Integer partnerId;

  @JsonProperty("src")
  private String callSource;  // "backoffice" or "partnerapi"

  @JsonProperty("bouser")
  private String backofficeUser;
}
