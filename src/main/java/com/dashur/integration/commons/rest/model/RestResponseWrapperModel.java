package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor()
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponseWrapperModel<T> {
  @JsonProperty("meta")
  private Map<String, Object> meta;

  @JsonProperty("error")
  private Map<String, Object> error;

  @JsonProperty("data")
  private T data;

  /**
   * check if has error.
   *
   * @return
   */
  public Boolean hasError() {
    if (Objects.nonNull(error) && !error.isEmpty()) {
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }

  /**
   * check if has data.
   *
   * @return
   */
  public boolean hasData() {
    if (Objects.nonNull(data)) {
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }
}
