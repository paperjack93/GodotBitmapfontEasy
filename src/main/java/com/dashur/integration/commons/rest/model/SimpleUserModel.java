package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Simplified user model, doesn't need all attributes. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleUserModel {
  /** The id of the User. */
  @JsonProperty("id")
  private Long id;

  /** The id of the Account. */
  @JsonProperty("account_id")
  private Long accountId;

  /** The name of the User */
  @JsonProperty("name")
  private String name;
}
