package com.dashur.integration.extw.connectors.everymatrix.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class WinRequest extends Request {
  @JsonProperty("Amount")
  private BigDecimal amount;

  @JsonProperty("Currency")
  private String currency;

  @JsonProperty("BonusId")
  private String bonusId;

  @JsonProperty("GameId")
  private String gameId;

  @JsonProperty("RoundId")
  private String roundId;

  @JsonProperty("ExternalId")
  private String externalId;

  @JsonProperty("BetExternalId")
  private String betExternalId;

  @JsonProperty("RoundEnd")
  private Boolean roundEnd;

  @JsonProperty("JackpotPayout")
  private List<JackpotPayout> jackpotPayouts;
}
