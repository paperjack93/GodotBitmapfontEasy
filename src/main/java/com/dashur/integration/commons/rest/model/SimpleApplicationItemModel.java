package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Simplified application item model, doesn't need all attributes. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleApplicationItemModel {
  /** The id of the ApplicationItem */
  @JsonProperty("id")
  private Long id;

  /** The id of the Application */
  @JsonProperty("app_id")
  private Long appId;

  /** The id of the Item */
  @JsonProperty("item_id")
  private Long itemId;

  /** The external reference from external provider */
  @JsonProperty("ext_ref")
  private String extRef;

  /** The status of the ApplicationItem, if the ApplicationItem is enabled or disabled. */
  @JsonProperty("status")
  private String status;

  /** This is a raw json map of additional data for the item. { "tags" : ["cool", "wicked"] */
  @JsonProperty("meta_data")
  private Map<String, Object> metaData;

  // Shortcut to retrieve item launch_id
  public String getLaunchId() {
    if (Objects.nonNull(metaData) && !metaData.isEmpty()) {
      Object value = metaData.get("launch_id");
      return value.toString();
    }

    return null;
  }
}
