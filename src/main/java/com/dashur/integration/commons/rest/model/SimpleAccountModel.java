package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Simplified account model, doesn't need all attributes. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleAccountModel {
  /** The id of the Account. */
  @JsonProperty("id")
  private Long id;

  /** Whether this is a test Account, default is false. */
  @JsonProperty("test")
  private Boolean test;

  /** Whether this is a test Account based on upline, default is false. */
  @JsonProperty("effective_test")
  private Boolean effectiveTest;

  /** The id chain of all the parent Accounts and current Account. */
  @JsonProperty("my_path")
  private String myPath;

  /** The name of the Account */
  @JsonProperty("name")
  private String name;

  /** The ext_ref of the account */
  @JsonProperty("ext_ref")
  private String extRef;

  /** @return company id of the current account. */
  public Long getCompanyId() {
    return Long.parseLong(myPath.split(",")[2]);
  }
}
