package com.dashur.integration.extw.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DasResponse {
  @JsonProperty("req_id")
  private String reqId;

  @JsonProperty("timestamp")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date timestamp;

  @JsonProperty("processing_time")
  private int processingTime;

  @JsonProperty("token")
  private String token;

  @JsonProperty("err_desc")
  private String errDesc;
}
