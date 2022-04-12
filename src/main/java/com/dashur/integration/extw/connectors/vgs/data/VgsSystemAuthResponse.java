package com.dashur.integration.extw.connectors.vgs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
@JacksonXmlRootElement(localName = "VGSSYSTEM")
public class VgsSystemAuthResponse extends VgsSystemResponse {
  @JacksonXmlProperty(localName = "REQUEST")
  private AuthRequest request;

  @JacksonXmlProperty(localName = "RESPONSE")
  private AuthResponse response;

  @Override
  public Request request() {
    return request;
  }

  @Override
  public Response response() {
    return response;
  }
}
