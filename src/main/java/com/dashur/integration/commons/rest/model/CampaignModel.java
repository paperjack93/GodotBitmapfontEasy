package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder(builderMethodName = "newCampaignModelBuilder")
@Wither
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignModel {

  public static final String METADATA_KEY_COST_PER_BET = "cost_per_bet";

  @JsonProperty("created_by")
  private Long createdBy;

  @JsonProperty("created")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date created;

  @JsonProperty("updated_by")
  private Long updatedBy;

  @JsonProperty("updated")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date updated;

  @JsonProperty("id")
  private Long id;

  @JsonProperty("account_id")
  private Long accountId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("ext_ref")
  private String extRef;

  @JsonProperty("vendor_ref")
  private String vendorRef;

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

  @JsonProperty("calculated_cost")
  private BigDecimal calculatedCost;

  @JsonProperty("start_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date startTime;

  @JsonProperty("end_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date endTime;

  @JsonProperty("games")
  @Valid
  private List<CampaignGamesModel> games;

  @JsonProperty("meta_data")
  @Valid
  private Map<String, Object> metaData;

  @JsonProperty("version")
  private Long version;

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

    FREE_GAMES("FREE_GAMES"),

    VOUCHER_GAMES("VOUCHER_GAMES");

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
