package com.dashur.integration.extw.connectors.vgs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Response {
  @JacksonXmlProperty(localName = "RESULT")
  private String result;

  @JacksonXmlProperty(localName = "CODE")
  private String code;
}
