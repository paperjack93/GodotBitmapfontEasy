package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaModel {
  @JsonProperty("currency")
  private String currency;

  @JsonProperty("time_zone")
  private String timezone;

  @JsonProperty("transaction_id")
  private String transactionId;

  @JsonProperty("processing_time")
  private Long processingTime;
}
