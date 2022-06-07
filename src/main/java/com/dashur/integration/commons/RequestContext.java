package com.dashur.integration.commons;

import com.dashur.integration.commons.auth.HashToken;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.utils.CommonUtils;
import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;

/**
 * RequestContext should be first generated very first on the controller end when it was invoked.
 */
@Getter
@Wither
@AllArgsConstructor
public class RequestContext implements Serializable {
  private static final String JWT_CTX_KEY = "ctx";

  private UUID uuid;
  private ZonedDateTime requestStartTime;
  private ZonedDateTime requestEndTime;

  private String accessToken; // dashur access token
  private HashToken hashToken; // hash token, for communication between vendors and integ
  private long accountId; // account id in dashur
  private long userId; // user id in dashur
  private String username; // user name in dashur
  private String currency; // user currency
  private String timezone; // requested timezone
  private String language; // requested language
  private String platform; // requested platform
  private Long applicationId; // token application id
  private Long tenantId; // token tenant id
  private Long linkedTenantId; // linked tenant id
  private String accountPath; // token account path

  private Map<String, Object> metaData;

  public RequestContext() {
    this.uuid = UUID.randomUUID();
    this.requestStartTime = ZonedDateTime.now();
    this.timezone = Constant.REST_HEADER_VALUE_DEFAULT_TZ;
    this.language = Constant.REST_HEADER_VALUE_DEFAULT_LANG;
    this.currency = Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY;
    this.metaData = new HashMap<>();
  }

  /** @return context instances. */
  public static RequestContext instance() {
    return new RequestContext();
  }

  /** Set current timestamp as request end time and get duration in return. */
  public Long setRequestEndTime() {
    if (Objects.isNull(this.requestEndTime)) {
      this.requestEndTime = ZonedDateTime.now();
    } else {
      throw new ApplicationException("Unable to modify requestEndTime. [%s]", this.requestEndTime);
    }

    return getDurationMs();
  }

  /** @return request duration in Ms. */
  public Long getDurationMs() {
    if (Objects.isNull(this.requestEndTime)) {
      setRequestEndTime();
    }

    return Duration.between(this.requestStartTime, this.requestEndTime).toMillis();
  }

  /** @return company id of the token's account. */
  public Long getCompanyId() {
    if (!CommonUtils.isEmptyOrNull(accountPath)) {
      return Long.parseLong(accountPath.split(",")[2]);
    }

    throw new ValidationException("Unable to retrieve company-id, accountPath is empty");
  }

  /** @return context id of the token's account. */
  public Long getContextId() {
    Map<String, Object> claims = CommonUtils.parseJwt(this.accessToken);
    return Long.valueOf(claims.getOrDefault(JWT_CTX_KEY, 0L).toString());
  }
}
