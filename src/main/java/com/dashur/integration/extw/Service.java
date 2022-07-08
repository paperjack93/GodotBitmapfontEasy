package com.dashur.integration.extw;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import java.util.Date;

public interface Service {
  /**
   * @param ctx
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @param applicationId
   * @param itemId
   * @param demo
   * @param launchToken
   * @return
   */
  String launchUrl(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      Long applicationId,
      Long itemId,
      Boolean demo,
      String launchToken,
      String lobbyUrl,
      String bankUrl);

  /**
   * @param ctx
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @param applicationId
   * @param itemId
   * @param demo
   * @param launchToken
   * @return
   */
  String launchUrl(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      Long applicationId,
      Long itemId,
      Boolean demo,
      String launchToken,
      String lobbyUrl,
      String bankUrl,
      String callerIp);

  /**
   * @param ctx
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @param roundId
   * @return
   */
  String playcheckUrl(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String roundId);

  /**
   * @param ctx
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @param vendorExtRef
   * @param itemId
   * @param noOfFreeRound
   * @param endDate
   * @return campaign.id
   */
  Long createOrUpdateCampaign(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String vendorExtRef,
      Long itemId,
      Integer noOfFreeRound,
      Date endDate);

  /**
   * @param ctx
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @param vendorExtRef
   * @param memberExtRef
   * @return campaign.id
   */
  Long addCampaignMember(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String vendorExtRef,
      String memberExtRef);

  /**
   * @param ctx
   * @param clientId
   * @param clientCredential
   * @param companyAppId
   * @param companyAppCredential
   * @param vendorExtRef
   * @param memberExtRef
   * @return campaign.id
   */
  Long delCampaignMember(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String vendorExtRef,
      String memberExtRef);
}
