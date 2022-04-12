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
public class ChangeBalanceRequest extends Request {
  @JacksonXmlProperty(localName = "USERID")
  private String userId;

  @JacksonXmlProperty(localName = "AMOUNT")
  private BigDecimal amount;

  @JacksonXmlProperty(localName = "TRANSACTIONID")
  private String transactionId;

  @JacksonXmlProperty(localName = "TRNTYPE")
  private String transactionType;

  @JacksonXmlProperty(localName = "GAMEID")
  private String gameId;

  @JacksonXmlProperty(localName = "ROUNDID")
  private String roundId;

  @JacksonXmlProperty(localName = "TRNDESCRIPTION")
  private String transactionDescription;

  @JacksonXmlProperty(localName = "HISTORY")
  private String history;

  @JacksonXmlProperty(localName = "ISROUNDFINISHED")
  private boolean roundFinished;
}
