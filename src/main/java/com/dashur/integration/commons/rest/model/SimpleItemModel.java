package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Simplified item model, doesn't need all attributes. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleItemModel {
  /** The id of the Item */
  @JsonProperty("id")
  private Long id;

  /** The application items of the item. */
  @JsonProperty("app_items")
  private List<SimpleApplicationItemModel> appItems;

  /** The id of the vendor this item belongs to */
  @JsonProperty("vendor_id")
  private Long vendorId;

  /** The name of the item */
  @JsonProperty("name")
  private String name;

  /** This is a raw json map of additional data for the item. { "tags" : ["cool", "wicked"] */
  @JsonProperty("meta_data")
  private Map<String, Object> metaData;
}
