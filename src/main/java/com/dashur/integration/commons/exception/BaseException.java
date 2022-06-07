package com.dashur.integration.commons.exception;

import static com.dashur.integration.commons.utils.CommonUtils.fmt;

import com.dashur.integration.commons.utils.CommonUtils;
import lombok.Getter;

/** BaseException for all integration codes, */
public class BaseException extends RuntimeException {
  /**
   * Main general exception code. This is to ensure easier mapping of application error codes to
   * integration vendors
   */
  @Getter
  public enum Code {
    APPLICATION(1000), // main category error code for all un known error.
    AUTH(1001),
    PAYMENT(1002),
    NOT_EXIST(1003),
    DUPLICATE(1004),
    STATUS(1005),
    VALIDATION(1006),
    TOO_MANY_REQUEST(1007);

    private int code;

    Code(int code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return String.valueOf(this.code);
    }

    /**
     * resolved by id
     *
     * @param code
     * @return
     */
    public static Code resolveById(int code) {
      switch (code) {
        case 1001:
          return AUTH;
        case 1002:
          return PAYMENT;
        case 1003:
          return NOT_EXIST;
        case 1004:
          return DUPLICATE;
        case 1005:
          return STATUS;
        case 1006:
          return VALIDATION;
        case 1007:
          return TOO_MANY_REQUEST;
        default: // include error 1000
          return APPLICATION;
      }
    }
  }

  private final int code;

  private final String subCode;

  /**
   * @param code
   * @param message
   */
  public BaseException(Code code, String message) {
    super(fmt("E-CODE:[%s] - %s", code.toString(), message));
    this.code = code.getCode();
    this.subCode = "";
  }

  /**
   * @param code
   * @param message
   * @param arg1
   */
  public BaseException(Code code, String message, Object arg1) {
    this(code, fmt(message, arg1));
  }

  /**
   * @param code
   * @param message
   * @param arg1
   * @param arg2
   */
  public BaseException(Code code, String message, Object arg1, Object arg2) {
    this(code, fmt(message, arg1, arg2));
  }

  /**
   * @param code
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public BaseException(Code code, String message, Object arg1, Object arg2, Object arg3) {
    this(code, fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param code
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public BaseException(
      Code code, String message, Object arg1, Object arg2, Object arg3, Object arg4) {
    this(code, fmt(message, arg1, arg2, arg3, arg4));
  }

  /**
   * @param code
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @param arg5
   */
  public BaseException(
      Code code, String message, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    this(code, fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /**
   * @param message
   * @param subCode
   * @param code
   */
  public BaseException(Code code, String subCode, String message) {
    super(fmt("E-CODE:[%s:%s] - %s", code.toString(), subCode, message));
    this.code = code.getCode();
    this.subCode = subCode;
  }

  /**
   * @param code
   * @param subCode
   * @param message
   * @param arg1
   */
  public BaseException(Code code, String subCode, String message, Object arg1) {
    this(code, subCode, fmt(message, arg1));
  }

  /**
   * @param code
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public BaseException(Code code, String subCode, String message, Object arg1, Object arg2) {
    this(code, subCode, fmt(message, arg1, arg2));
  }

  /**
   * @param code
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public BaseException(
      Code code, String subCode, String message, Object arg1, Object arg2, Object arg3) {
    this(code, subCode, fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param code
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public BaseException(
      Code code,
      String subCode,
      String message,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4) {
    this(code, subCode, fmt(message, arg1, arg2, arg3, arg4));
  }

  /**
   * @param code
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   * @param arg5
   */
  public BaseException(
      Code code,
      String subCode,
      String message,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5) {
    this(code, subCode, fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /** @return */
  public Code getCode() {
    return Code.resolveById(this.code);
  }

  /** @return */
  public String getSubCode() {
    return this.subCode;
  }

  /**
   * get code that will be used for i18n message arguments
   *
   * @return
   */
  public String i18nCode() {
    if (this.code == 0 && CommonUtils.isWhitespaceOrNull(this.subCode)) {
      return Code.APPLICATION.toString();
    }

    if (CommonUtils.isWhitespaceOrNull(this.subCode)) {
      return String.valueOf(this.code);
    }

    return fmt("%s:%s", this.code, this.subCode);
  }
}
