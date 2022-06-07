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
@Builder(builderMethodName = "newCampaignVoucherModelBuilder")
@Wither
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignVoucherModel {
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

  /** The id of campaign voucher */
  @JsonProperty("id")
  private Long id;

  /** The id of the campaign related to this voucher */
  @JsonProperty("campaign_id")
  private Long campaignId;

  /** The id of campaign member related to this voucher */
  @JsonProperty("campaign_member_id")
  private Long campaignMemberId;

  /** The account id of the campaign member */
  @JsonProperty("account_id")
  private Long accountId;

  /** The external_ref of the campaign member */
  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  /** The unique voucher id/ref from integrator */
  @JsonProperty("voucher_ref")
  private String voucherRef;

  /** The id of game which the voucher is valid for */
  @JsonProperty("game_id")
  private Long gameId;

  /** The number of games which the voucher issued for */
  @JsonProperty("num_of_games")
  private Integer numOfGames;

  /**
   * The status of campaign voucher. NEW = voucher is newly issued CLAIMED = voucher has been
   * successfully claimed
   */
  @JsonProperty("status")
  private Status status;

  /** The version of the entity, any changes to the entity will increase this number */
  @JsonProperty("version")
  private Long version;

  public enum Status {
    NEW("NEW"),

    CLAIMED("CLAIMED");

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
