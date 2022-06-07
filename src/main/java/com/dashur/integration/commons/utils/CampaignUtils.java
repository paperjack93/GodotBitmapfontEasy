package com.dashur.integration.commons.utils;

import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.rest.model.CampaignModelExt;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/** Utilities for campaign related stuff */
@Slf4j
public class CampaignUtils {

  // key for kv store for campaign without game_id
  // [vendor_id]::campaign::1::[campaign_id]
  private static final String KEY_CAMPAIGN_FORMAT = "%s::campaign::1::%s";

  // key for kv store for multi-games campaign by campaign_id and game_id
  // [vendor_id]::campaign::1::[campaign_id]::[game_id]
  private static final String KEY_CAMPAIGN_GAME_FORMAT = "%s::campaign::1::%s::%s";

  // this will store all members that are participating in the campaign
  // [vendor_id]::campaign::3::[campaign_id] => set(account_id)
  private static final String KEY_CAMPAIGN_MEMBERS_FORMAT = "%s::campaign::3::%s";

  // this will store all member assignment references
  // the key should be unique with member ext_id and the value will be vendor-dependent
  // [vendor_id]::campaign::6::[assignment_key]
  private static final String KEY_CAMPAIGN_ASSIGN_FORMAT = "%s::campaign::6::%s";

  public static String formatKey(
      String vendorCode, CampaignModelExt campaign, boolean includeGame) {
    if (includeGame) {
      if (CommonUtils.isEmptyOrNull(vendorCode)
          || (Objects.isNull(campaign.getId()) || campaign.getId() == 0)
          || (Objects.isNull(campaign.getGameId()) || campaign.getGameId() == 0)) {
        throw new ValidationException(
            "input is empty or null vendorCode:[%s] - campaignId:[%s] - gameId:[%s]",
            vendorCode, campaign.getId(), campaign.getGameId());
      }

      return String.format(
          KEY_CAMPAIGN_GAME_FORMAT, vendorCode, campaign.getId(), campaign.getGameId());
    } else {
      if (CommonUtils.isEmptyOrNull(vendorCode)
          || (Objects.isNull(campaign.getId()) || campaign.getId() == 0)) {
        throw new ValidationException(
            "input is empty or null vendorCode:[%s] - campaignId:[%s]",
            vendorCode, campaign.getId());
      }

      return String.format(KEY_CAMPAIGN_FORMAT, vendorCode, campaign.getId());
    }
  }

  public static String formatMemberKey(String vendorCode, Long campaignId) {
    if (CommonUtils.isEmptyOrNull(vendorCode) || (Objects.isNull(campaignId) || campaignId == 0)) {
      throw new ValidationException(
          "input is empty or null vendorCode:[%s] - campaignId:[%s]", vendorCode, campaignId);
    }

    return String.format(KEY_CAMPAIGN_MEMBERS_FORMAT, vendorCode, campaignId);
  }

  public static String formatAssignKey(String vendorCode, String assignKey) {
    if (CommonUtils.isEmptyOrNull(vendorCode) || CommonUtils.isEmptyOrNull(assignKey)) {
      throw new ValidationException(
          "input is empty or null vendorCode:[%s] - assignKey:[%s]", vendorCode, assignKey);
    }

    return String.format(KEY_CAMPAIGN_ASSIGN_FORMAT, vendorCode, assignKey);
  }

  public static Long deriveTtl(CampaignModelExt campaign) {
    if (Objects.isNull(campaign) || Objects.isNull(campaign.getEndTime())) {
      return null;
    }

    LocalDateTime end =
        campaign
            .getEndTime()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .plusDays(1L); // add 1 days to end day for longer keeping of campaign.

    // Unit tests are mostly having expired date-time, so need extra handling to work properly
    if (end.isBefore(LocalDateTime.now())) {
      end = LocalDateTime.now().plusDays(1);
    }

    return Duration.between(LocalDateTime.now(), end).toMillis();
  }
}
