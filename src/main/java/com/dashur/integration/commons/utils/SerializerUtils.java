package com.dashur.integration.commons.utils;

import com.dashur.integration.commons.Constant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.NoArgsConstructor;

/** ZonedDateTime object serializer */
public class SerializerUtils {

  @NoArgsConstructor
  public static class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
    private DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss 'UTC'ZZ");

    public ZonedDateTimeSerializer(DateTimeFormatter formatter) {
      this.formatter = formatter;
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      ZonedDateTime parsed = value.withZoneSameInstant(Constant.DEFAULT_TIMEZONE);
      gen.writeString(formatter.format(parsed));
    }
  }

  @NoArgsConstructor
  public static class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
    private DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss 'UTC'ZZ");

    public ZonedDateTimeDeserializer(DateTimeFormatter formatter) {
      this.formatter = formatter;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      ZonedDateTime parsed = ZonedDateTime.parse(p.getText(), formatter);
      return parsed.withZoneSameInstant(Constant.DEFAULT_TIMEZONE);
    }
  }
}
