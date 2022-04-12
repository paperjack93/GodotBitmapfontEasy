package com.dashur.integration.extw.data;

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
public class DasEndRoundRequest extends DasRequest {
  @JsonProperty("currency")
  private String currency;

  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  @JsonProperty("tx_id")
  private long txId;

  @JsonProperty("application_id")
  private long applicationId;

  @JsonProperty("item_id")
  private long itemId;

  @JsonProperty("round_id")
  private String roundId;

  @JsonProperty("txs")
  private List<Long> txs;

  @JsonProperty("round_stats")
  private List<DasRoundStat> roundStats;

  @JsonProperty("start_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date startTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @JsonProperty("close_time")
  private Date closeTime;

  @JsonProperty("revenue")
  private BigDecimal revenue;
}
