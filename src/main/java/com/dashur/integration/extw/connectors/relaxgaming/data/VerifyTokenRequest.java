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
public class VerifyTokenRequest extends Request {

  @JsonProperty("channel")
  private String channel;

  @JsonProperty("clientid")
  private String clientId;  

  @JsonProperty("token")
  private String token;  

  @JsonProperty("gameref")
  private String gameRef;  

  @JsonProperty("partnerid")
  private Integer partnerId;

  @JsonProperty("ip")
  private String ip;

  // optional

  @JsonProperty("sessiontimeout")
  private Integer sessionTimeout;

  @JsonProperty("rcinterval")
  private Integer rcInterval;

  @JsonProperty("rcelapsed")
  private Integer rcElapsed;

  @JsonProperty("rchistoryurl")
  private String rcHistoryUrl;

}
