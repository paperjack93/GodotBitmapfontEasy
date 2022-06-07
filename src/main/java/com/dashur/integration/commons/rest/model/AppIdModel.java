package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppIdModel {
  @JsonProperty("trustedFacets")
  private List<TrustedFacet> trustedFacets;

  public AppIdModel() {
    trustedFacets = new ArrayList<>();
  }

  public AppIdModel(List<String> ids) {
    this();
    TrustedFacet trustedFacet = new TrustedFacet();
    trustedFacet.setIds(ids);
    trustedFacets.add(trustedFacet);
  }

  @Data
  @ToString
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TrustedFacet {
    @JsonProperty("version")
    private Version version;

    @JsonProperty("ids")
    private List<String> ids;

    public TrustedFacet() {
      this.version = new Version();
      this.ids = new ArrayList<>();
    }
  }

  @Data
  @ToString
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Version {
    @JsonProperty("major")
    private int major;

    @JsonProperty("minor")
    private int minor;

    public Version() {
      this.major = 1;
      this.minor = 0;
    }
  }
}
