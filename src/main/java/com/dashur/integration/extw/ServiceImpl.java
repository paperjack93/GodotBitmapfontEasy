package com.dashur.integration.extw;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.domain.CommonService;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import java.util.Date;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ServiceImpl implements Service {
  @Inject ExtwIntegConfiguration config;
  @Inject CommonService commonService;
  @Inject DomainService domainService;

  @Override
  public String launchUrl(
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
      String bankUrl) {
    return launchUrl(
        ctx,
        clientId,
        clientCredential,
        companyAppId,
        companyAppCredential,
        applicationId,
        itemId,
        demo,
        launchToken,
        lobbyUrl,
        bankUrl,
        null);
  }

  @Override
  public String launchUrl(
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
      String callerIp) {
    ctx =
        ctx.withAccessToken(
            commonService.companyAppAccessToken(
                ctx, clientId, clientCredential, companyAppId, companyAppCredential));
    return domainService.extWalletLaunch(
        ctx, launchToken, applicationId, itemId, demo, lobbyUrl, bankUrl, callerIp);
  }

  public String playcheckUrl(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String roundId) {
    ctx =
        ctx.withAccessToken(
            commonService.companyAppAccessToken(
                ctx, clientId, clientCredential, companyAppId, companyAppCredential));

    TransactionRoundModel round = domainService.findTransactionRoundByRoundExtRef(ctx, roundId);

    if (Objects.isNull(round)) {
      throw new EntityNotExistException("Unable to find round");
    }

    return domainService.extWalletPlaycheck(ctx, round.getId());
  }

  @Override
  public Long createOrUpdateCampaign(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String vendorExtRef,
      Long itemId,
      Integer noOfFreeRound,
      Date endDate) {
    return null;
  }

  @Override
  public Long addCampaignMember(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String vendorExtRef,
      String memberExtRef) {
    return null;
  }

  @Override
  public Long delCampaignMember(
      RequestContext ctx,
      String clientId,
      String clientCredential,
      String companyAppId,
      String companyAppCredential,
      String vendorExtRef,
      String memberExtRef) {
    return null;
  }
}
