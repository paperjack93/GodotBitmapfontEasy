package com.dashur.integration.commons.exception;

import static com.dashur.integration.commons.utils.CommonUtils.fmt;

import lombok.Getter;

/** Payment exception. */
public class PaymentException extends BaseException {
  /** Payment sub codes, for further categorisation of exception by integ class. */
  @Getter
  public enum SubCode {
    UNKNOWN_PAYMENT_ERROR(9100),
    REMOTE_PAYMENT_ERROR(9102),
    BALANCE_NOT_ENOUGH(9103),
    UNSUPPORTED_CURRENCY(9104),
    UNSUPPORTED_AMOUNT(9105);

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
        case 9101:
          return UNKNOWN_PAYMENT_ERROR;
        case 9102:
          return REMOTE_PAYMENT_ERROR;
        case 9103:
          return BALANCE_NOT_ENOUGH;
        case 9104:
          return UNSUPPORTED_CURRENCY;
        case 9105:
          return UNSUPPORTED_AMOUNT;
        default: // include 9100
          return UNKNOWN_PAYMENT_ERROR;
      }
    }
  }

  /** @param message */
  public PaymentException(String message) {
    super(Code.PAYMENT, SubCode.UNKNOWN_PAYMENT_ERROR.toString(), message);
  }

  /**
   * @param message
   * @param arg1
   */
  public PaymentException(String message, Object arg1) {
    this(fmt(message, arg1));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   */
  public PaymentException(String message, Object arg1, Object arg2) {
    this(fmt(message, arg1, arg2));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public PaymentException(String message, Object arg1, Object arg2, Object arg3) {
    this(fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public PaymentException(String message, Object arg1, Object arg2, Object arg3, Object arg4) {
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
  public PaymentException(
      String message, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    this(fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /**
   * @param subCode
   * @param message
   */
  public PaymentException(SubCode subCode, String message) {
    super(Code.PAYMENT, subCode.toString(), message);
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   */
  public PaymentException(SubCode subCode, String message, Object arg1) {
    this(subCode, fmt(message, arg1));
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public PaymentException(SubCode subCode, String message, Object arg1, Object arg2) {
    this(subCode, fmt(message, arg1, arg2));
  }

  /**
   * @param subCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public PaymentException(SubCode subCode, String message, Object arg1, Object arg2, Object arg3) {
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
  public PaymentException(
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
  public PaymentException(
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
  public PaymentException(int httpCode, String message) {
    super(
        Code.PAYMENT,
        String.format("%s:%s", SubCode.REMOTE_PAYMENT_ERROR.toString(), httpCode),
        message);
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   */
  public PaymentException(int httpCode, String message, Object arg1) {
    this(httpCode, fmt(message, arg1));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public PaymentException(int httpCode, String message, Object arg1, Object arg2) {
    this(httpCode, fmt(message, arg1, arg2));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public PaymentException(int httpCode, String message, Object arg1, Object arg2, Object arg3) {
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
  public PaymentException(
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
  public PaymentException(
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
  public PaymentException(int httpCode, int domainCode, String message) {
    super(
        Code.PAYMENT,
        String.format("%s:%s:%s", SubCode.REMOTE_PAYMENT_ERROR.toString(), httpCode, domainCode),
        message);
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   */
  public PaymentException(int httpCode, int domainCode, String message, Object arg1) {
    this(httpCode, domainCode, fmt(message, arg1));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public PaymentException(int httpCode, int domainCode, String message, Object arg1, Object arg2) {
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
  public PaymentException(
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
  public PaymentException(
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
  public PaymentException(
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
