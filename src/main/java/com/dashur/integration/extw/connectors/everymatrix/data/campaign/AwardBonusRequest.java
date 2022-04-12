package com.dashur.integration.extw.connectors.everymatrix.data.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AwardBonusRequest {
  @JsonProperty("UserId")
  private String userId;

  @JsonProperty("BonusId")
  private String bonusId;

  @JsonProperty("GameIds")
  private List<String> gameIds;

  @JsonProperty("NumberOfFreeRounds")
  private Integer numberOfFreeRounds;

  @JsonProperty("Currency")
  private String currency;

  @JsonProperty("CoinValues")
  private BigDecimal coinValue;

  @JsonProperty("BetValueLevel")
  private Integer betValueLevel;

  @JsonProperty("LineCount")
  private Integer lineCount;

  @JsonProperty("FreeRoundsEndDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date freeRoundsEndDate;
}
