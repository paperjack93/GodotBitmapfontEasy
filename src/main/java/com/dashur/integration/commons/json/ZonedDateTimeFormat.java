package com.dashur.integration.commons.json;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate what format a ZonedDateTime should be serialized to The default is Format.FULL
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface ZonedDateTimeFormat {

  Format format() default Format.FULL;

  /** Enum of all valid serialization formats for dates in Dashur */
  enum Format {
    /** yyyy-MM-dd 2015-05-26 */
    DATE("yyyy-MM-dd"),
    /** yyyy-MM-ddTHH:mm:ss 2015-05-26T13:23:12 */
    DATE_TIME("yyyy-MM-dd'T'HH:mm:ss"),
    /** yyyy-MM-ddTHH:mm:ss 'UTC'ZZ 2015-05-26T13:23:12 UTC+0100 */
    FULL("yyyy-MM-dd'T'HH:mm:ss 'UTC'ZZ"),
    /** yyyy-MM-dd HH:mm:ss.SSS */
    MILLI("yyyy-MM-dd HH:mm:ss.SSS");

    /** The format for each type */
    private String formatString;

    Format(String format) {
      this.formatString = format;
    }

    public String getFormatString() {
      return formatString;
    }
  }
}
