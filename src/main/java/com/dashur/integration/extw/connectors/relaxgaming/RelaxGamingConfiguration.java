package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.AbstractOperatorConfiguration;
import com.dashur.integration.extw.Configuration;
import com.dashur.integration.extw.Constant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;

@Getter
@Slf4j
public class RelaxGamingConfiguration extends AbstractOperatorConfiguration {
  static final String CONFIG_PREFIX = "extw.operator.relaxgaming";

  private String operator;

  private Long defaultCompanyId;
  private boolean debug;
  private boolean validateIps;
  private Set<String> whitelistIps;
  private String platform;
  private String gamestudio;
  private String rgsProvider;
  private Map<String, Long> operatorIdMap;
  private Map<Long, CompanySetting> companySettings;

  public RelaxGamingConfiguration() {
    this.operator = Constant.OPERATOR_RELAXGAMING;
    this.operatorIdMap = new HashMap<>();
    this.companySettings = new HashMap<>();
    this.whitelistIps = new HashSet<>();
    this.debug = true;
    this.validateIps = false;
  }

  @Override
  public String operator() {
    return this.operator;
  }

  @Override
  public Configuration configure(Config config) {
    defaultCompanyId = value(CONFIG_PREFIX, "default-company-id", Long.class, 1L, config);
    debug = value(CONFIG_PREFIX, "debug", Boolean.class, Boolean.FALSE, config);
    platform = value(CONFIG_PREFIX, 
      "platform", String.class, UUID.randomUUID().toString(), config);
    gamestudio = value(CONFIG_PREFIX, 
      "gamestudio", String.class, UUID.randomUUID().toString(), config);
    rgsProvider = value(CONFIG_PREFIX, 
      "rgs.provider", String.class, UUID.randomUUID().toString(), config);
    validateIps = value(CONFIG_PREFIX, "ip.validate", Boolean.class, Boolean.FALSE, config);
    String ips = value(CONFIG_PREFIX, "ip.whitelist", String.class, "", config);

    if (!CommonUtils.isEmptyOrNull(ips)) {
      String[] ipAddrs = ips.split(",");
      for (String ip : ipAddrs) {
        if (!CommonUtils.isEmptyOrNull(ip)) {
          whitelistIps.add(ip.trim());
        }
      }
    }

    for (String key : config.getPropertyNames()) {
      {
        String prefix = String.format("%s.%s.", CONFIG_PREFIX, "operator-id-mapping");
        if (key.startsWith(prefix)) {
          String code = key.replace(prefix, "");
          Long value =
              value(CONFIG_PREFIX, "operator-id-mapping.%s", Long.class, null, config, code);

          if (Objects.isNull(value)) {
            log.error("Some error with the following configuration [{}] - value is not found", key);
          } else {
            operatorIdMap.put(code, value);
          }
        }
      }
    }

    Set<Long> companyIds = new HashSet<>(operatorIdMap.values());

    for (Long companyId : companyIds) {
      Long applicationId =
          value(
              CONFIG_PREFIX, "co.%s.application-id", Long.class, 0L, config, companyId.toString());
      Integer partnerId =
          value(
              CONFIG_PREFIX,
              "co.%s.partner-id",
              Integer.class,
              10,
              config,
              companyId.toString());
      String channel =
          value(
              CONFIG_PREFIX,
              "co.%s.channel",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String operatorCredential =
          value(
              CONFIG_PREFIX,
              "co.%s.operator-credential",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String hmacKey =
          value(
              CONFIG_PREFIX,
              "co.%s.hmac-key",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String clientId =
          value(
              CONFIG_PREFIX,
              "co.%s.client-id",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String clientCredential =
          value(
              CONFIG_PREFIX,
              "co.%s.client-credential",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String apiId =
          value(
              CONFIG_PREFIX,
              "co.%s.api-id",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String apiCredential =
          value(
              CONFIG_PREFIX,
              "co.%s.api-credential",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String remoteBaseUri =
          value(
              CONFIG_PREFIX,
              "co.%s.remote-base-uri",
              String.class,
              "",
              config,
              companyId.toString());

      CompanySetting setting =
          CompanySetting.builder()
              .companyId(companyId)
              .launcherItemApplicationId(applicationId)
              .partnerId(partnerId)
              .channel(channel)
              .operatorCredential(operatorCredential)
              .hmacKey(hmacKey)
              .launcherAppClientId(clientId)
              .launcherAppClientCredential(clientCredential)
              .launcherAppApiId(apiId)
              .launcherAppApiCredential(apiCredential)
              .remoteBaseUri(remoteBaseUri)
              .build();

      companySettings.put(companyId, setting);
    }

    log.debug("relax config: {}", CommonUtils.jsonToString(this));
    return this;
  }

  @Builder
  @Getter
  public static class CompanySetting {
    private Long companyId;
    private Long launcherItemApplicationId;
    private Integer partnerId;
    private String channel;
    private String operatorCredential;
    private String hmacKey;
    private String launcherAppClientId;
    private String launcherAppClientCredential;
    private String launcherAppApiId;
    private String launcherAppApiCredential;
    private String remoteBaseUri;
  }
}
