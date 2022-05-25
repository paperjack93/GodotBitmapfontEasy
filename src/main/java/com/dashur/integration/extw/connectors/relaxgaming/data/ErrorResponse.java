package com.dashur.integration.extw.connectors.relaxgaming.data;

import java.util.List;
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
public class ErrorResponse {

  @JsonProperty("errorcode")
  private String code;

  @JsonProperty("errormessage")
  private String message;

  @JsonProperty("events")
  private List<Object> events;
}
