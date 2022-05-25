package com.dashur.integration.extw.connectors.relaxgaming.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DepositRequest extends TransactionRequest {

  @JsonProperty("channel")
  private String channel;

  // optional

  @JsonProperty("jppayout")
  List<JackpotPayout> jpPayout;

  @JsonProperty("promocode")
  String promoCode;     // promotional identifier for freespinspayouts

  @JsonProperty("freespinsid")
  String freespinsId;

  @JsonProperty("promotionid")
  String promotionId;     // prefix rlx.<platform>  only specified with txtype=promopayout

  @JsonProperty("betamount")
  Long betAmount;         // corresponding bet to this deposit. Required for PokerStars

  @JsonProperty("replayurl")
  String replayUrl;       // url to public reachable replay page. Required only for PokerStars

}
