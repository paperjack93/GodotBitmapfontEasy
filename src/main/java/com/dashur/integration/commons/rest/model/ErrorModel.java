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
public class ErrorModel {
  @JsonProperty("meta")
  private MetaModel meta;

  @JsonProperty("error")
  private ErrorModel.DataModel error;

  @Data
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class DataModel {
    @JsonProperty("type")
    private String type;

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;
  }
}
