package com.dashur.integration.commons;

import javax.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
@Slf4j
public class CommonsConfig {
  @ConfigProperty(name = "commons.cache.license_key", defaultValue = "redison-license-key")
  String cacheLicenseKey;

  @ConfigProperty(name = "commons.cache.host", defaultValue = "localhost")
  String cacheHost;

  @ConfigProperty(name = "commons.cache.port", defaultValue = "6379")
  int cachePort;

  @ConfigProperty(name = "commons.kv.host", defaultValue = "localhost")
  String kvHost;

  @ConfigProperty(name = "commons.kv.port", defaultValue = "6379")
  int kvPort;

  @ConfigProperty(name = "commons.tx.retrymillis", defaultValue = "100")
  Long txRetryMillis;
}
