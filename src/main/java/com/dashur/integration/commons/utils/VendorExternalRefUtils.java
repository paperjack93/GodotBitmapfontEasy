package com.dashur.integration.commons.utils;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.exception.ValidationException;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/** Utils to form the external-reference. */
@Slf4j
public class VendorExternalRefUtils {
  private static final Object LOCKS = new Object();
  private static final Map<String, Long> VENDOR_ID_MAP = Maps.newHashMap();

  static {
    init();
  }

  /** initialise mapping */
  private static final void init() {
    try {
      String env = System.getenv(Constant.ENV_KEY_VENDOR_ID_MAPPING);
      if (!CommonUtils.isEmptyOrNull(env)) {
        VENDOR_ID_MAP.putAll(CommonUtils.jsonReadMap(env, String.class, Long.class));
      }
    } catch (Exception e) {
      log.error("Unable to initialize external-ref mapping", e);
    }
  }

  /**
   * @param vendorCode
   * @param vendorId
   */
  public static final void setup(String vendorCode, Long vendorId) {
    if (CommonUtils.isEmptyOrNull(vendorCode)) {
      throw new ValidationException("vendorCode is empty or null");
    }

    if (Objects.isNull(vendorId)) {
      throw new ValidationException("vendorId is empty or null: [%s]", vendorId);
    }

    if (!VENDOR_ID_MAP.containsKey(vendorCode)) {
      synchronized (LOCKS) {
        if (!VENDOR_ID_MAP.containsKey(vendorCode)) {
          VENDOR_ID_MAP.put(vendorCode, vendorId);
        }
      }
    }
  }

  /**
   * @param vendorCode
   * @param vendorExternalRef
   * @return
   */
  public static final String toDashurExternalRef(String vendorCode, String vendorExternalRef) {
    if (CommonUtils.isEmptyOrNull(vendorCode)) {
      throw new ValidationException("vendorCode is empty or null");
    }

    if (CommonUtils.isEmptyOrNull(vendorExternalRef)) {
      throw new ValidationException("externalRef is empty or null");
    }

    if (!VENDOR_ID_MAP.containsKey(vendorCode)) {
      throw new ValidationException("vendorCode [%s] is not configured", vendorCode);
    }

    if (VENDOR_ID_MAP.get(vendorCode) <= 0L) {
      throw new ValidationException(
          "vendorCode [%s] is not configured, its value is [%s]",
          vendorCode, VENDOR_ID_MAP.get(vendorCode));
    }

    return String.format("%s-%s", VENDOR_ID_MAP.get(vendorCode), vendorExternalRef);
  }

  /**
   * @param vendorCode
   * @param dashurExternalRef
   * @return
   */
  public static final String toVendorExternalRef(String vendorCode, String dashurExternalRef) {
    if (CommonUtils.isEmptyOrNull(vendorCode)) {
      throw new ValidationException("vendorCode is empty or null");
    }

    if (CommonUtils.isEmptyOrNull(dashurExternalRef)) {
      throw new ValidationException("dashurExternalRef is empty or null");
    }

    if (!VENDOR_ID_MAP.containsKey(vendorCode)) {
      throw new ValidationException("vendorCode [%s] is not configured", vendorCode);
    }

    if (VENDOR_ID_MAP.get(vendorCode) <= 0L) {
      throw new ValidationException(
          "vendorCode [%s] is not configured, its value is [%s]",
          vendorCode, VENDOR_ID_MAP.get(vendorCode));
    }

    return dashurExternalRef.replaceFirst(String.format("%s-", VENDOR_ID_MAP.get(vendorCode)), "");
  }
}
