package com.dashur.integration.commons.json;

import com.dashur.integration.commons.Constant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Objects;

/**
 * De serialize ZonedDateTime objects.
 *
 * <p>This class reuses ZonedDateTimeConverter so the same logic applies
 */
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
  /**
   * This formatter allows the following formats
   *
   * <p>
   *
   * <ul>
   *   <li>2015-11-01 -> yyyy-MM-dd
   *   <li>2015-11-01T17:12:45 -> yyyy-MM-ddTHH:mm:ss
   *   <li>2015-11-01T17:12:45 +0800 -> yyyy-MM-ddTHH:mm:ss ZZ
   *   <li>2015-11-01T17:12:45 UTC+0800 -> yyyy-MM-ddTHH:mm:ss 'UTC'ZZ, please note only UTC is
   *       accepted
   *   <li>2015-11-01 17:12:45.000 -> yyyy-MM-dd HH:mm:ss.SSS
   */
  private static DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd[['T'][' ']HH:mm:ss[.SSS][ ['UTC']ZZ]]");

  private final ZoneId fixZoneId;

  public ZonedDateTimeDeserializer(ZoneId fixZoneId) {
    this.fixZoneId = fixZoneId;
  }

  /**
   * @param parser
   * @param ctxt
   * @return
   * @throws IOException
   */
  @Override
  public ZonedDateTime deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    try {
      return parse(parser.getValueAsString(), null);
    } catch (ParseException pxe) {
      throw new JsonParseException(pxe.getMessage(), parser.getCurrentLocation());
    }
  }

  /**
   * @param text
   * @param fixZoneId
   * @return
   */
  ZonedDateTime parseWithFixTimeZone(String text, ZoneId fixZoneId) {
    ZoneId zoneId = Objects.isNull(fixZoneId) ? Constant.DEFAULT_TIMEZONE : fixZoneId;
    TemporalAccessor date =
        formatter.parseBest(text, ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
    if (date instanceof LocalDate) {
      date = ((LocalDate) date).atStartOfDay(zoneId);
    } else if (date instanceof LocalDateTime) {
      date = ((LocalDateTime) date).atZone(zoneId);
    }
    return ((ZonedDateTime) date).withZoneSameInstant(Constant.DEFAULT_TIMEZONE);
  }

  /**
   * @param text
   * @param locale
   * @return
   * @throws ParseException
   */
  ZonedDateTime parse(String text, Locale locale) throws ParseException {
    try {
      /** Get the timezone from the request context RequestContext gurantess not to return a null */
      return parseWithFixTimeZone(text, fixZoneId);
    } catch (DateTimeParseException dte) {
      ParseException pxe =
          new ParseException(
              String.format(
                  "Error parsing date/time (%s) something went wrong at %d",
                  text, dte.getErrorIndex()),
              dte.getErrorIndex());
      pxe.addSuppressed(dte);
      throw pxe;
    }
  }
}
