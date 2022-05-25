package com.dashur.integration.extw.connectors.relaxgaming.data.service;

import java.util.List;
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
public class JackpotValuesResponse {

  @JsonProperty("id")
  private String jackpotId;

  @JsonProperty("amount")
  private Long amount;

  @JsonProperty("games")
  private List<String> games;

  @JsonProperty("name")
  private String name;
  
  @JsonProperty("currency")
  private String currency;

}
