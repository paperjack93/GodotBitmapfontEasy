package com.dashur.integration.extw.rgs.data;

import com.dashur.integration.commons.rest.model.TransactionFeedModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class PlaycheckExtRequest {

  @JsonProperty("feeds")
  private List<TransactionFeedModel> feeds;
  
}
