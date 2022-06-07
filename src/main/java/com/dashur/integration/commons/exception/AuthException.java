package com.dashur.integration.commons.exception;

import static com.dashur.integration.commons.utils.CommonUtils.fmt;

import lombok.Getter;

/** Authentication exception. */
public class AuthException extends BaseException {

  /** Auth sub codes, for further categorisation of exception by integ class. */
  @Getter
  public enum SubCode {
    UNKNOWN_AUTH_ERROR(9000), // error code arises from handling authentication mechanism.
    REMOTE_AUTH_ERROR(9001), // error code arises from handling remote authentication mechanism.
    ILLEGAL_TOKEN(9002), // error code arises from handling conversion from and to token handling
    ILLEGAL_ARGUMENT(9003); // error code arises from handling operations with regards to
    // authentication/authorisation operations due to check and validation.

    private int code;

    /** @param code */
    SubCode(int code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return String.valueOf(code);
    }

    /**
     * resolve by id
     *
     * @param code
     * @return
     */
    public static SubCode resolveById(int code) {
      switch (code) {
        case 9001:
          return REMOTE_AUTH_ERROR;
        case 9002:
          return ILLEGAL_TOKEN;
        case 9003:
          return ILLEGAL_ARGUMENT;
        default: // include 9000
          return UNKNOWN_AUTH_ERROR;
      }
    }
  }

  /** @param message */
  public AuthException(String message) {
    super(Code.AUTH, SubCode.UNKNOWN_AUTH_ERROR.toString(), message);
  }

  /**
   * @param message
   * @param arg1
   */
  public AuthException(String message, Object arg1) {
    this(fmt(message, arg1));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   */
  public AuthException(String message, Object arg1, Object arg2) {
    this(fmt(message, arg1, arg2));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public AuthException(String message, Object arg1, Object arg2, Object arg3) {
    this(fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public AuthException(String message, Object arg1, Object arg2, Object arg3, Object arg4) {
    this(fmt(message, arg1, arg2, arg3, arg4));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @param arg5
   */
  public AuthException(
      String message, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    this(fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /**
   * @param subCode
   * @param message
   */
  public AuthException(SubCode subCode, String message) {
    super(Code.AUTH, subCode.toString(), message);
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   */
  public AuthException(SubCode subCode, String message, Object arg1) {
    this(subCode, fmt(message, arg1));
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public AuthException(SubCode subCode, String message, Object arg1, Object arg2) {
    this(subCode, fmt(message, arg1, arg2));
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public AuthException(SubCode subCode, String message, Object arg1, Object arg2, Object arg3) {
    this(subCode, fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public AuthException(
      SubCode subCode, String message, Object arg1, Object arg2, Object arg3, Object arg4) {
    this(subCode, fmt(message, arg1, arg2, arg3, arg4));
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @param arg5
   */
  public AuthException(
      SubCode subCode,
      String message,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5) {
    this(subCode, fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /**
   * @param httpCode
   * @param message
   */
  public AuthException(int httpCode, String message) {
    super(
        Code.AUTH, String.format("%s:%s", SubCode.REMOTE_AUTH_ERROR.toString(), httpCode), message);
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   */
  public AuthException(int httpCode, String message, Object arg1) {
    this(httpCode, fmt(message, arg1));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public AuthException(int httpCode, String message, Object arg1, Object arg2) {
    this(httpCode, fmt(message, arg1, arg2));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public AuthException(int httpCode, String message, Object arg1, Object arg2, Object arg3) {
    this(httpCode, fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public AuthException(
      int httpCode, String message, Object arg1, Object arg2, Object arg3, Object arg4) {
    this(httpCode, fmt(message, arg1, arg2, arg3, arg4));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @param arg5
   */
  public AuthException(
      int httpCode,
      String message,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5) {
    this(httpCode, fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   */
  public AuthException(int httpCode, int domainCode, String message) {
    super(
        Code.AUTH,
        String.format("%s:%s:s", SubCode.REMOTE_AUTH_ERROR.toString(), httpCode, domainCode),
        message);
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   */
  public AuthException(int httpCode, int domainCode, String message, Object arg1) {
    this(httpCode, domainCode, fmt(message, arg1));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public AuthException(int httpCode, int domainCode, String message, Object arg1, Object arg2) {
    this(httpCode, domainCode, fmt(message, arg1, arg2));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public AuthException(
      int httpCode, int domainCode, String message, Object arg1, Object arg2, Object arg3) {
    this(httpCode, domainCode, fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public AuthException(
      int httpCode,
      int domainCode,
      String message,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4) {
    this(httpCode, domainCode, fmt(message, arg1, arg2, arg3, arg4));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @param arg5
   */
  public AuthException(
      int httpCode,
      int domainCode,
      String message,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5) {
    this(httpCode, domainCode, fmt(message, arg1, arg2, arg3, arg4, arg5));
  }
}
