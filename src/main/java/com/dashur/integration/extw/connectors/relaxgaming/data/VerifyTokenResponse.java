package com.dashur.integration.extw.connectors.relaxgaming.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
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
public class VerifyTokenResponse extends Response {

  @JsonProperty("playerid")
  private Integer playerId;

  @JsonProperty("customerId")
  private String customerId;

  @JsonProperty("username")
  private String userName;

  @JsonProperty("locale")
  private String locale;

  // ISO 3166-1 alpha 2
  @JsonProperty("countrycode")
  private String counteryCode;

  @JsonProperty("gender")
  private Integer gender;

  // ISO 4217
  @JsonProperty("currency")
  private String currency;

  // ISO 8601
  @JsonProperty("birthdate")
  private String birthDate;

  // ISO 8601
  @JsonProperty("lastlogin")
  private String lastLogin;

  @JsonProperty("jurisdiction")
  private String jurisdiction;

  @JsonProperty("balance")
  private Long balance;   // in cents

  @JsonProperty("partnerid")
  private Integer partnerId;

  // conversion rate of selected currency in euro
  @JsonProperty("currencyrate")
  private BigDecimal currencyRate;

  // optional

  @JsonProperty("promotions")
  private List<Promotion> promotions;

}