package com.dashur.integration.extw;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;

public abstract class AbstractOperatorConfiguration implements Configuration {
  /**
   * return value from configuration with default
   *
   * @param key
   * @param type
   * @param defaultValue
   * @param config
   * @param <T>
   * @return
   */
  protected <T> T value(String key, Class<T> type, T defaultValue, Config config) {
    Optional<T> result = config.getOptionalValue(key, type);

    if (result.isPresent()) {
      return result.get();
    }

    return defaultValue;
  }

  /**
   * return value from configuration with default and configures key.
   *
   * @param prefix
   * @param key
   * @param type
   * @param defaultValue
   * @param config
   * @param keyArgs
   * @param <T>
   * @return
   */
  protected <T> T value(
      String prefix, String key, Class<T> type, T defaultValue, Config config, String... keyArgs) {
    String finalKey = null;

    if (Objects.nonNull(keyArgs) && keyArgs.length > 0) {
      finalKey = String.format("%s.%s", prefix, String.format(key, keyArgs));
    } else {
      finalKey = String.format("%s.%s", prefix, key);
    }

    return value(finalKey, type, defaultValue, config);
  }
}
