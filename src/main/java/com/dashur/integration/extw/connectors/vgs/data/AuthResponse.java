package com.dashur.integration.extw.connectors.vgs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AuthResponse extends Response {
  @JacksonXmlProperty(localName = "USERID")
  private String userId;

  @JacksonXmlProperty(localName = "USERNAME")
  private String username;

  @JacksonXmlProperty(localName = "FIRSTNAME")
  private String firstname;

  @JacksonXmlProperty(localName = "LASTNAME")
  private String lastname;

  @JacksonXmlProperty(localName = "EMAIL")
  private String email;

  @JacksonXmlProperty(localName = "CURRENCY")
  private String currency;

  @JacksonXmlProperty(localName = "BALANCE")
  private BigDecimal balance;

  @JacksonXmlProperty(localName = "GAMESESSIONID")
  private String gameSessionId;
}
