package com.dashur.integration.extw.connectors.everymatrix;

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
public class EveryMatrixConfiguration extends AbstractOperatorConfiguration {
  static final String CONFIG_PREFIX = "extw.operator.everymatrix";

  private String operator;

  private String mode;
  private Long defaultCompanyId;
  private boolean debug;
  private boolean validateIps;
  private Set<String> whitelistIps;
  private Map<String, Long> operatorIdMap;
  private Map<Long, CompanySetting> companySettings;

  public EveryMatrixConfiguration() {
    this.operator = Constant.OPERATOR_EVERYMATRIX;
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
    mode = value(CONFIG_PREFIX, "mode", String.class, "dev", config);
    defaultCompanyId = value(CONFIG_PREFIX, "default-company-id", Long.class, 1L, config);
    debug = value(CONFIG_PREFIX, "debug", Boolean.class, Boolean.FALSE, config);
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
      String provider =
          value(
              CONFIG_PREFIX,
              "co.%s.provider",
              String.class,
              UUID.randomUUID().toString(),
              config,
              companyId.toString());
      String hashSecret =
          value(
              CONFIG_PREFIX,
              "co.%s.hash-secret",
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

      CompanySetting setting =
          CompanySetting.builder()
              .companyId(companyId)
              .launcherItemApplicationId(applicationId)
              .hashSecret(hashSecret)
              .hmacKey(hmacKey)
              .provider(provider)
              .launcherAppClientId(clientId)
              .launcherAppClientCredential(clientCredential)
              .launcherAppApiId(apiId)
              .launcherAppApiCredential(apiCredential)
              .build();

      companySettings.put(companyId, setting);
    }

    return this;
  }

  /** @return */
  public CompanySetting getDefaultCompanySetting() {
    return companySettings.get(defaultCompanyId);
  }

  @Builder
  @Getter
  public static class CompanySetting {
    private Long companyId;
    private Long launcherItemApplicationId;
    private String provider;
    private String hashSecret;
    private String hmacKey;
    private String launcherAppClientId;
    private String launcherAppClientCredential;
    private String launcherAppApiId;
    private String launcherAppApiCredential;
  }
}
