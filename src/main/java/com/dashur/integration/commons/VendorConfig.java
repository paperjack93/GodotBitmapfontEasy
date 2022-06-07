package com.dashur.integration.commons;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.commons.utils.VendorExternalRefUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
@Slf4j
public class VendorConfig {
  @ConfigProperty(name = "integ.version", defaultValue = "v1.0")
  String version;

  @ConfigProperty(name = "integ.operator", defaultValue = "Organization")
  String operator;

  @ConfigProperty(name = "integ.jurisdiction", defaultValue = "NULL")
  String jurisdiction;

  @ConfigProperty(name = "integ.test.companies", defaultValue = "1,2,3")
  List<String> testCompanyIds;

  @ConfigProperty(name = "integ.test.mode", defaultValue = "false")
  Boolean testMode;

  @ConfigProperty(name = "integ.merchant.api.user", defaultValue = "user")
  String merchantApiUser;

  @ConfigProperty(name = "integ.merchant.api.password", defaultValue = "password")
  String merchantApiPassword;

  @ConfigProperty(name = "integ.merchant.wallet.user", defaultValue = "user")
  String merchantWalletUser;

  @ConfigProperty(name = "integ.merchant.wallet.password", defaultValue = "password")
  String merchantWalletPassword;

  Map<String, VendorInfo> vendorInfoMap;

  @PostConstruct
  public void init() {
    Config config = ConfigProvider.getConfig();

    vendorInfoMap = new ConcurrentHashMap<>();
    String[] vendors = config.getValue("vendor.codes", String[].class);

    for (String vendor : vendors) {
      VendorInfo info = new VendorInfo();
      info.setCode(vendor);
      info.setVendorId(config.getValue(String.format("vendor.%s.id", vendor), Long.class));
      VendorExternalRefUtils.setup(info.getCode(), info.getVendorId());

      try {
        info.setValidateIps(
            config.getValue(String.format("vendor.%s.ip.validate", vendor), Boolean.class));
        info.setWhitelistIps(
            new HashSet<>(
                Arrays.asList(
                    config.getValue(
                        String.format("vendor.%s.ip.whitelist", vendor), String[].class))));
        info.setApiClientId(
            config.getValue(String.format("vendor.%s.api.client.id", vendor), String.class));
        info.setApiClientCredential(
            config.getValue(
                String.format("vendor.%s.api.client.credential", vendor), String.class));
      } catch (NoSuchElementException e) {
        log.warn("VendorConfig property not found. {}", e.getMessage());
      }

      info.setDefaultPlatform(
          config.getValue(String.format("vendor.%s.platform.default", vendor), String.class));

      String[] platforms =
          config.getValue(String.format("vendor.%s.platforms", vendor), String[].class);
      Map<String, PlatformInfo> platformMap = new HashMap<>();
      for (String platform : platforms) {
        PlatformInfo platformInfo = new PlatformInfo();
        platformInfo.setCode(platform);
        platformInfo.setAppId(
            config.getValue(
                String.format("vendor.%s.platform.app.%s.id", vendor, platform), String.class));
        platformInfo.setAppCredential(
            config.getValue(
                String.format("vendor.%s.platform.app.%s.credential", vendor, platform),
                String.class));
        platformMap.put(platform, platformInfo);
      }
      info.setPlatforms(platformMap);

      vendorInfoMap.put(vendor, info);
    }
  }

  /**
   * Get vendor info by code
   *
   * @param vendor The vendor code
   * @return The vendor info
   */
  public VendorInfo vendorInfo(String vendor) {
    if (CommonUtils.isWhitespaceOrNull(vendor)) {
      throw new ValidationException(
          "VendorConfig.vendorInfo(vendor) => [%s] is null or empty", vendor);
    }

    if (vendorInfoMap.containsKey(vendor)) {
      return vendorInfoMap.get(vendor);
    }

    throw new ApplicationException("VendorConfig.vendorInfo(%s) => is not found", vendor);
  }

  /**
   * Get vendor info by id
   *
   * @param vendorId The vendor id
   * @return The vendor info
   */
  public VendorInfo vendorInfo(Long vendorId) {
    if (Objects.isNull(vendorId)) {
      throw new ValidationException("VendorConfig.vendorInfo(id) => [%s] is null", vendorId);
    }

    return vendorInfoMap.values().stream()
        .filter(vendor -> vendor.getVendorId().equals(vendorId))
        .findFirst()
        .orElseThrow(
            () ->
                new ApplicationException("VendorConfig.vendorInfo(%s) => is not found", vendorId));
  }

  public boolean isTestCompany(RequestContext ctx) {
    if (Objects.nonNull(ctx) && !CommonUtils.isWhitespaceOrNull(ctx.getUsername())) {
      return isTestCompany(ctx.getUsername());
    }

    return false;
  }

  public boolean isTestCompany(String username) {
    if (Objects.isNull(testCompanyIds) || testCompanyIds.isEmpty()) {
      return false;
    }

    if (CommonUtils.isWhitespaceOrNull(username)) {
      return false;
    }

    String[] usernameParts = username.split(":");
    if (usernameParts.length < 2) {
      return false;
    }

    String companyTag = usernameParts[1];
    return testCompanyIds.contains(companyTag);
  }

  @Data
  public static final class VendorInfo {
    String code;
    Long vendorId;
    String auth;
    boolean validateIps;
    Set<String> whitelistIps;
    String apiClientId;
    String apiClientCredential;
    String defaultPlatform;
    Map<String, PlatformInfo> platforms;

    @JsonIgnore
    public PlatformInfo platformInfo(String platform) {
      if (CommonUtils.isWhitespaceOrNull(platform)) {
        throw new ValidationException(
            "VendorInfo.platformInfo(platform) => [%s] is null or empty", platform);
      }

      if (platforms.containsKey(platform)) {
        return platforms.get(platform);
      }

      throw new ApplicationException("VendorInfo.platformInfo(%s) => is not found", platform);
    }

    @JsonIgnore
    public PlatformInfo platformInfo() {
      return platformInfo(defaultPlatform);
    }

    @JsonIgnore
    public List<String> getCIDRList() {
      return whitelistIps.stream().filter(ip -> ip.contains("/")).collect(Collectors.toList());
    }
  }

  @Data
  public static final class PlatformInfo {
    String code;
    String appId;
    String appCredential;
  }
}
