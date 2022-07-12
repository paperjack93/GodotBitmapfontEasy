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
import java.time.ZonedDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class FreeRound {

  @JsonProperty("freespinvalue")
  private Long freespinValue; // in cents

  @JsonProperty("expires")
  private ZonedDateTime expires;     // ISO 8601

  @JsonProperty("promocode")
  private String promoCode;

  @JsonProperty("gameref")
  private String gameRef;

  @JsonProperty("amount")
  private Integer amount; // amount of rounds. valid range 1-5000

  @JsonProperty("freespinsid")
  private String freespinsId;

  @JsonProperty("createtime")
  private ZonedDateTime createTime;

  @JsonProperty("currency")
  private String currency;

}