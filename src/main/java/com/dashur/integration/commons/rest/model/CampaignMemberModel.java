package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder(builderMethodName = "newCampaignMemberModelBuilder")
@Wither
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignMemberModel {
  /** The id of the user who created the entity */
  @JsonProperty("created_by")
  private Long createdBy;

  /** The time the entity was created */
  @JsonProperty("created")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date created;

  /** The id of the user who updated the entity */
  @JsonProperty("updated_by")
  private Long updatedBy;

  /** The time the entity was updated */
  @JsonProperty("updated")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private Date updated;

  /** The id of campaign member assignment */
  @JsonProperty("id")
  private Long id;

  /** The id of the campaign */
  @JsonProperty("campaign_id")
  private Long campaignId;

  /** The account id of the member */
  @JsonProperty("account_id")
  private Long accountId;

  /** The account ext ref of the member */
  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  /** The external id of the member, for idempotency purpose */
  @JsonProperty("ext_id")
  private String extId;

  /** The assignment status of the member */
  @JsonProperty("status")
  private Status status;

  /** The id of game which the player has been awarded */
  @JsonProperty("game_id")
  private Long gameId;

  /** The version of the entity, any changes to the entity will increase this number */
  @JsonProperty("version")
  private Long version;

  public enum Status {
    NEW("NEW"),

    AWARDED("AWARDED");

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
}
