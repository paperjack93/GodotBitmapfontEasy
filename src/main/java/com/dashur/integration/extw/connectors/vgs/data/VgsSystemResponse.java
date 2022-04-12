package com.dashur.integration.extw.connectors.vgs.data;

import com.dashur.integration.commons.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.Date;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class VgsSystemResponse {
  @JacksonXmlProperty(localName = "TIME")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
  private Date time;

  public Request request() {
    throw new ApplicationException("Not implemented");
  }

  public Response response() {
    throw new ApplicationException("Not implemented");
  }
}
