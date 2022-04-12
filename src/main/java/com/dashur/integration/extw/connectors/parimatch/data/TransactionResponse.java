package com.dashur.integration.extw.connectors.parimatch.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class TransactionResponse extends Response {

  @JsonProperty("txId")
  private String txId;

  @JsonProperty("processedTxId")
  private String extwTxId;

  @JsonProperty("createdAt")
  private String createdAt; // yyyy-MM-dd’T’HH:mm:ss.SSSSSSZ

  @JsonProperty("alreadyProcessed")
  private Boolean processed;
}
