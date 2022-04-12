package com.dashur.integration.extw.connectors.everymatrix.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AuthenticateResponse extends Response {
  @JsonProperty("Token")
  private String token;

  @JsonProperty("TotalBalance")
  private BigDecimal totalBalance;

  @JsonProperty("Currency")
  private String currency;

  @JsonProperty("UserName")
  private String username;

  @JsonProperty("UserId")
  private String userId;

  @JsonProperty("Country")
  private String country;

  @JsonProperty("Age")
  private Integer age;

  @JsonProperty("Sex")
  private String sex;
}
