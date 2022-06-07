package com.dashur.integration.commons.json;

import com.dashur.integration.commons.Constant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Serialize ZonedDateTime objects
 *
 * <p>The format can be configured via ZonedDateTimeFormat annotaions
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime>
    implements ContextualSerializer {

  private DateTimeFormatter formatter;

  private ZoneId fixZoneId = null;

  /** Need an empty constructor for Jackson */
  public ZonedDateTimeSerializer() {}

  public ZonedDateTimeSerializer(ZonedDateTimeFormat.Format format, ZoneId fixZoneId) {
    this.formatter = DateTimeFormatter.ofPattern(format.getFormatString());
    this.fixZoneId = fixZoneId;
  }

  public ZonedDateTimeSerializer(ZoneId fixZoneId) {
    this.fixZoneId = fixZoneId;
  }

  /**
   * Jackson uses createContextual to get a configured Serializer
   *
   * <p>In this case depending on the ZonedDateTimeFormat annotation the serializer will choose the
   * format to serialize the ZonedDateTime into FULL, DATE and DATE_TIME are options
   */
  @Override
  public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
      throws JsonMappingException {

    ZonedDateTimeFormat.Format format = ZonedDateTimeFormat.Format.DATE;
    if (property != null && property.getAnnotation(ZonedDateTimeFormat.class) != null) {
      format = property.getAnnotation(ZonedDateTimeFormat.class).format();
    }
    return new ZonedDateTimeSerializer(format, fixZoneId);
  }

  @Override
  public void serialize(ZonedDateTime dateTime, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {

    ZoneId zoneId = Objects.isNull(fixZoneId) ? Constant.DEFAULT_TIMEZONE : fixZoneId;
    jgen.writeString(formatter.format(dateTime.withZoneSameInstant(zoneId)));
  }
}
