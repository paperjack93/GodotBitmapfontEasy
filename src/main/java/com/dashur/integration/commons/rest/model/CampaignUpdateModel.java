package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder(builderMethodName = "newCampaignUpdateModelBuilder")
@Wither
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignUpdateModel {

  @JsonProperty("name")
  private String name;

  @JsonProperty("num_of_games")
  private Integer numOfGames;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("type")
  private Type type;

  @JsonProperty("game_id")
  private Long gameId;

  @JsonProperty("bet_level")
  private Integer betLevel;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("start_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date startTime;

  @JsonProperty("end_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date endTime;

  @JsonProperty("meta_data")
  @Valid
  private Map<String, Object> metaData;

  @JsonProperty("version")
  private Long version;

  public CampaignUpdateModel putMetaDataItem(String key, Object metaDataItem) {
    if (Objects.isNull(metaData)) {
      metaData = new HashMap<>();
    }

    if (Objects.nonNull(metaDataItem)) {
      metaData.put(key, metaDataItem);
    }

    return this;
  }

  public enum Status {
    ACTIVE("ACTIVE"),

    SUSPENDED("SUSPENDED"),

    CLOSED("CLOSED");

    private String value;

    Status(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static Status fromValue(String text) {
      for (Status b : Status.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  public enum Type {
    UNKNOWN("UNKNOWN"),

    FREE_GAMES("FREE_GAMES");

    private String value;

    Type(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static Type fromValue(String text) {
      for (Type b : Type.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
}
