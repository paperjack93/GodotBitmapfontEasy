package com.dashur.integration.commons.utils;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.auth.AuthorizationType;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * A simple common utilities. To minimise 3rd party library usages at the sametime allow easier
 * program refactoring if future decision changed.
 */
@Slf4j
public class CommonUtils {
  private static final String JWT_EXP_KEY = "exp";

  private static final ObjectMapper jsonMapper;
  private static final XmlMapper xmlMapper;
  private static final String jsonDateFormat = "dd-MM-yyyy hh:mm:ss";

  static {
    jsonMapper = new ObjectMapper();
    jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonMapper.setPropertyNamingStrategy(
        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    jsonMapper.setDateFormat(new SimpleDateFormat(jsonDateFormat));

    xmlMapper = new XmlMapper();
    xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    xmlMapper.configure(Feature.WRITE_XML_DECLARATION, true);
    xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    xmlMapper.setDateFormat(new SimpleDateFormat(jsonDateFormat));
  }

  /**
   * Can be used when different configurations are needed. Cached serializers and deserializers are
   * NOT shared, which means that the new instance may be re-configured before use
   */
  public static ObjectMapper getJsonMapperCopy() {
    return jsonMapper.copy();
  }

  /**
   * check whether string is empty or null
   *
   * @param data The string to check
   * @return The status whether the string is empty or null
   */
  public static Boolean isEmptyOrNull(String data) {
    if (Objects.isNull(data) || data.equals("")) return Boolean.TRUE;

    return Boolean.FALSE;
  }

  /**
   * check whether string is only contain whitespaces or null.
   *
   * @param data The string to check
   * @return The status whether the string is whitespace or null
   */
  public static Boolean isWhitespaceOrNull(String data) {
    if (Objects.isNull(data) || data.trim().equals("")) return Boolean.TRUE;

    return Boolean.FALSE;
  }

  /**
   * converting data to int, throw exception when fail
   *
   * @param data The string to convert
   * @return Value in Integer or 0
   */
  public static Integer toIntegerOrZero(String data) {
    if (isEmptyOrNull(data)) return 0;

    try {
      return Integer.parseInt(data);
    } catch (NumberFormatException e) {
      throw new ValidationException("[%s] is not number, cannot parse to Integer", data);
    }
  }

  /**
   * @param data The string to convert
   * @return Value in BigDecimal or 0
   */
  public static BigDecimal toBigDecimalOrZero(String data) {
    if (isEmptyOrNull(data)) return BigDecimal.ZERO;

    if (data.contains(",")) {
      data = data.replace(",", "");
    }

    try {
      return new BigDecimal(data);
    } catch (NumberFormatException e) {
      throw new ValidationException("[%s] is not number, cannot parse to BigDecimal", data);
    }
  }

  /**
   * @param input The value to convert
   * @return Money value with 2 decimal points or 0
   */
  public static BigDecimal toMoney(BigDecimal input) {
    if (Objects.isNull(input)) {
      return BigDecimal.ZERO;
    }

    return input.setScale(2, RoundingMode.HALF_EVEN);
  }

  /**
   * @param data The string to convert
   * @return Value in Long or 0
   */
  public static Long toLongOrZero(String data) {
    if (isEmptyOrNull(data)) return 0L;

    try {
      return Long.parseLong(data);
    } catch (NumberFormatException e) {
      throw new ValidationException("[%s] is not number, cannot parse to Long", data);
    }
  }

  /**
   * @param data The string to convert
   * @return Value in Boolean or null
   */
  public static Boolean toBooleanOrNull(String data) {
    if (isEmptyOrNull(data)) return null;

    try {
      return Boolean.getBoolean(data);
    } catch (IllegalArgumentException e) {
      throw new ValidationException("[%s] is not boolean, cannot parse to Boolean", data);
    }
  }

  /**
   * @param username The username
   * @param password The password
   * @return The string representation of Basic Auth
   */
  public static final String authorizationBasic(String username, String password) {
    if (isWhitespaceOrNull(username))
      throw new ValidationException("username: [%s] is not whitespace or null", username);

    if (isWhitespaceOrNull(password))
      throw new ValidationException("password: [%s] is not whitespace or null", password);

    return String.format(
        "%s %s",
        AuthorizationType.BASIC.toString(),
        Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes()));
  }

  /**
   * @param accessToken The access token
   * @return The string representation of Bearer Token
   */
  public static final String authorizationBearer(String accessToken) {
    if (isWhitespaceOrNull(accessToken))
      throw new ValidationException("accessToken: [%s] is whitespace or null", accessToken);

    return String.format("%s %s", Constant.REST_AUTH_AUTHORIZATION_TYPE_BEARER, accessToken);
  }

  /**
   * @param plainText The text to encode
   * @return The encoded string
   */
  public static String base64Encode(String plainText) {
    return BaseEncoding.base64().encode(plainText.getBytes()).replaceAll("=*$", "");
  }

  /**
   * @param codeText The text to decode
   * @return The decoded string
   */
  public static String base64Decode(String codeText) {
    return new String(BaseEncoding.base64().decode(codeText), StandardCharsets.UTF_8);
  }

  /**
   * @param data The object to convert
   * @return The map representation of the object
   */
  public static Map<String, Object> jsonConvertMap(Object data) {
    String content = jsonToString(data);
    return jsonReadMap(content);
  }

  /**
   * @param content The json string to convert
   * @return The map representation of the object
   */
  public static Map<String, Object> jsonReadMap(String content) {
    return jsonReadMap(content, String.class, Object.class);
  }

  /**
   * read json
   *
   * @param type The target class type
   * @param content The json string to convert
   * @param <V> The target object type
   * @return The target object
   */
  public static <V> V jsonRead(Class<V> type, String content) {
    try {
      return jsonMapper.readValue(content, type);
    } catch (IOException e) {
      log.error("failed to read json {} as {}", content, type, e);
      throw new ApplicationException("unable to read json [%s]", content);
    }
  }

  /**
   * @param content The json string
   * @param valueType The target class type
   * @param <T> The target object type
   * @return The list representation of the object
   */
  public static <T> List<T> jsonReadList(String content, Class<T> valueType) {
    if (Strings.isNullOrEmpty(content)) {
      return Collections.emptyList();
    }
    JavaType type = jsonMapper.getTypeFactory().constructCollectionType(List.class, valueType);
    try {
      return jsonMapper.readValue(content, type);
    } catch (IOException e) {
      log.error("failed to read json {} as list of {}", content, valueType.getName(), e);
      String typeName = String.format("List<%s>", valueType.getName());
      throw new ApplicationException("unable to read json [%s] as [%s]", content, typeName);
    }
  }

  /**
   * @param content The json string
   * @param keyType The target class type of Key
   * @param valueType The target class type of Value
   * @param <K> The target object type of Key
   * @param <V> The target object type of Value
   * @return The map representation of the object
   */
  public static <K, V> Map<K, V> jsonReadMap(String content, Class<K> keyType, Class<V> valueType) {
    if (Strings.isNullOrEmpty(content)) {
      return Collections.emptyMap();
    }
    JavaType type = jsonMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType);
    try {
      return jsonMapper.readValue(content, type);
    } catch (IOException e) {
      log.error(
          "failed to read json {} as map of {},{}",
          content,
          keyType.getName(),
          valueType.getName(),
          e);
      String typeName = String.format("Map<%s,%s>", keyType.getName(), valueType.getName());
      throw new ApplicationException("unable to read json [%s] as [%s]", content, typeName);
    }
  }

  public static <V> V objectReadMap(Map<String, Object> map, Class<V> type) {
    if (Objects.isNull(map) || map.isEmpty()) {
      return null;
    }
    try {
      String json = jsonMapper.writeValueAsString(map);
      return jsonMapper.readValue(json, type);
    } catch (IOException e) {
      log.error("failed to read map {} as {}", map, type, e);
      throw new ApplicationException("unable to convert map [%s] to object", map);
    }
  }

  /**
   * @param data The object to convert
   * @return The json string
   */
  public static String jsonToString(Object data) {
    try {
      return jsonMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new ApplicationException("unable to convert data [%s] to json", data);
    }
  }

  /**
   * read xml
   *
   * @param type The target class type
   * @param content The xml string to convert
   * @param <V> The target object type
   * @return The target object
   */
  public static <V> V xmlRead(Class<V> type, String content) {
    try {
      return xmlMapper.readValue(content, type);
    } catch (IOException e) {
      log.error("failed to read xml {} as {}", content, type, e);
      throw new ApplicationException("unable to read xml [%s]", content);
    }
  }

  /**
   * @param data The object to convert
   * @return The xml string
   */
  public static String xmlToString(Object data) {
    try {
      return xmlMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new ApplicationException("unable to convert data [%s] to xml", data);
    }
  }

  /**
   * @param key
   * @param locale
   * @param args
   * @return
   */
  public static final String getI18nMessages(String key, Locale locale, Object... args) {
    if (Objects.isNull(locale)) {
      locale = Locale.ENGLISH;
    }
    ResourceBundle bundle = ResourceBundle.getBundle("message", locale);
    return MessageFormat.format(bundle.getString(key), args);
  }

  /**
   * @param languageCode
   * @return
   */
  public static Locale parseLocale(String languageCode) {
    if (!CommonUtils.isWhitespaceOrNull(languageCode)) {
      if (languageCode.contains("_")) {
        String[] parts = languageCode.split("_");
        return new Locale(parts[0], parts[1]);
      } else if (languageCode.contains("-")) {
        String[] parts = languageCode.split("-");
        return new Locale(parts[0], parts[1]);
      } else {
        return new Locale(languageCode);
      }
    }

    return Locale.ENGLISH;
  }

  /**
   * resolving ip address.
   *
   * @return
   */
  public static String resolveIpAddress(HttpRequest request) {
    if (Objects.nonNull(request)) {
      { // if http is proxied via CF, this header should be available.
        String cfConnectingIp = request.getHttpHeaders().getHeaderString("cf-connecting-ip");
        if (!CommonUtils.isWhitespaceOrNull(cfConnectingIp)) {
          if (cfConnectingIp.contains(",")) {
            log.trace(
                "cf-connecting-ip contains multiple ip: [{}], will take first on as IP",
                cfConnectingIp);
            return cfConnectingIp.split(",")[0].trim();
          }
          return cfConnectingIp.trim();
        }
      }
      { // if http is on GKE environment, this header should be available
        String xOriginalForwardedFor =
            request.getHttpHeaders().getHeaderString("x-original-forwarded-for");
        if (!CommonUtils.isWhitespaceOrNull(xOriginalForwardedFor)) {
          if (xOriginalForwardedFor.contains(",")) {
            log.trace(
                "x-original-forwarded-for contains multiple ip: [{}], will take first on as IP",
                xOriginalForwardedFor);
            return xOriginalForwardedFor.split(",")[0].trim();
          }
          return xOriginalForwardedFor.trim();
        }
      }
      { // when service behind proxy (ingress or something), this header should be available.
        String xForwardedFor = request.getHttpHeaders().getHeaderString("x-forwarded-for");
        if (!CommonUtils.isWhitespaceOrNull(xForwardedFor)) {
          if (xForwardedFor.contains(",")) {
            log.trace(
                "x-forwarded-for contains multiple ip: [{}], will take first on as IP",
                xForwardedFor);
            return xForwardedFor.split(",")[0].trim();
          }
          return xForwardedFor.trim();
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("Unable to find ip from headers. check if its really empty.");
        for (Map.Entry keypair : request.getHttpHeaders().getRequestHeaders().entrySet()) {
          log.debug("headers: [{}] - [{}]", keypair.getKey(), keypair.getValue());
        }
      }

      return request.getRemoteAddress();
    }
    throw new ApplicationException("request is null");
  }

  /**
   * convert amount to cents
   *
   * @param amount
   * @return
   */
  public static Long toCents(BigDecimal amount) {
    if (Objects.isNull(amount)) {
      return 0L;
    }

    return amount.multiply(new BigDecimal(100)).longValue();
  }

  /**
   * convert from cent long amount to BigDecimal
   *
   * @param cents
   * @return
   */
  public static BigDecimal fromCents(Long cents) {
    if (Objects.isNull(cents)) {
      return BigDecimal.ZERO;
    }

    return new BigDecimal(cents).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
  }

  /**
   * @param input
   * @return
   */
  public static String str(Object input) {
    if (Objects.isNull(input)) return "null";

    return input.toString();
  }

  /**
   * format strings
   *
   * @param message
   * @param args
   * @return
   */
  public static String fmt(String message, Object... args) {
    if (Objects.nonNull(args) && args.length > 0) {
      return String.format(message, args);
    }

    return message;
  }

  /**
   * parsing JWT without verify it.
   *
   * @param jwt
   * @return
   */
  public static Map<String, Object> parseJwt(String jwt) {
    if (!isEmptyOrNull(jwt)) {
      try {
        int idxStart = jwt.indexOf(".");
        int idxEnd = jwt.lastIndexOf(".");
        String base64Token = jwt.substring(idxStart + 1, idxEnd);
        String json = new String(Base64.getDecoder().decode(base64Token));
        return jsonReadMap(json);
      } catch (Exception e) {
        log.trace("Unable to parse JWT");
      }
    }

    return Maps.newHashMap();
  }

  /**
   * will return true, if jwt not valid or expired
   *
   * @param jwt
   * @return
   */
  public static Boolean isTokenExpired(String jwt) {
    return isTokenExpired(jwt, ZonedDateTime.now(Constant.DEFAULT_TIMEZONE));
  }

  /**
   * will return true, if jwt not valid or expired
   *
   * @param jwt
   * @param now
   * @return
   */
  public static Boolean isTokenExpired(String jwt, ZonedDateTime now) {
    Map<String, Object> claims = parseJwt(jwt);

    if (claims.containsKey(JWT_EXP_KEY) && claims.get(JWT_EXP_KEY) instanceof Integer) {
      Integer intValue = (Integer) claims.get(JWT_EXP_KEY);
      ZonedDateTime expiredAt =
          ZonedDateTime.ofInstant(Instant.ofEpochSecond(intValue), Constant.DEFAULT_TIMEZONE);

      if (now.isBefore(expiredAt)) {
        return Boolean.FALSE;
      }
    }

    return Boolean.TRUE;
  }

  @AllArgsConstructor
  @Data
  public static final class Pair<K, V> {
    private K key;
    private V value;
  }
}
