package com.dashur.integration.commons.exception;

import static com.dashur.integration.commons.utils.CommonUtils.fmt;

/** Entity duplicate. when trying to perform operations on entity that is already exist. */
public class DuplicateException extends BaseException {
  /** @param message */
  public DuplicateException(String message) {
    super(Code.DUPLICATE, message);
  }

  /**
   * @param message
   * @param arg1
   */
  public DuplicateException(String message, Object arg1) {
    this(fmt(message, arg1));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   */
  public DuplicateException(String message, Object arg1, Object arg2) {
    this(fmt(message, arg1, arg2));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public DuplicateException(String message, Object arg1, Object arg2, Object arg3) {
    this(fmt(message, arg1, arg2, arg3));
  }

  /**
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   * @param arg4
   */
  public DuplicateException(String message, Object arg1, Object arg2, Object arg3, Object arg4) {
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
  public DuplicateException(
      String message, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    this(fmt(message, arg1, arg2, arg3, arg4, arg5));
  }

  /**
   * @param httpCode
   * @param message
   */
  public DuplicateException(int httpCode, String message) {
    super(Code.DUPLICATE, String.valueOf(httpCode), message);
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   */
  public DuplicateException(int httpCode, String message, Object arg1) {
    this(httpCode, fmt(message, arg1));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public DuplicateException(int httpCode, String message, Object arg1, Object arg2) {
    this(httpCode, fmt(message, arg1, arg2));
  }

  /**
   * @param httpCode
   * @param message
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public DuplicateException(int httpCode, String message, Object arg1, Object arg2, Object arg3) {
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
  public DuplicateException(
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
  public DuplicateException(
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
  public DuplicateException(int httpCode, int domainCode, String message) {
    super(Code.DUPLICATE, String.format("%s:%s", httpCode, domainCode), message);
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   */
  public DuplicateException(int httpCode, int domainCode, String message, Object arg1) {
    this(httpCode, domainCode, fmt(message, arg1));
  }

  /**
   * @param httpCode
   * @param domainCode
   * @param message
   * @param arg1
   * @param arg2
   */
  public DuplicateException(
      int httpCode, int domainCode, String message, Object arg1, Object arg2) {
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
  public DuplicateException(
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
  public DuplicateException(
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
  public DuplicateException(
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
