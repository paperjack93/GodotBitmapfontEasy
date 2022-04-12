package com.dashur.integration.extw.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DasMoney {
  @JsonProperty("currency")
  private String currency;

  @JsonProperty("amount")
  private BigDecimal amount;
}
