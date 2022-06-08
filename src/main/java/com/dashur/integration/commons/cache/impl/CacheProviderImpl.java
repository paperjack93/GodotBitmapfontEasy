package com.dashur.integration.commons.cache.impl;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.cache.CacheProvider;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.client.RedisTimeoutException;
import org.redisson.config.Config;

/** Cache provider. */
@Slf4j
@Singleton
public class CacheProviderImpl implements CacheProvider {
  final ReentrantReadWriteLock clientInitLock = new ReentrantReadWriteLock();
  final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

  @Inject CommonsConfig config;

  private Config clientConfig;
  private RedissonClient client;
  private AtomicInteger clientVersion;
  private AtomicInteger prevClientVersion;
  private Map<String, CacheConfig> cacheConfigMap;
  private Map<String, RLocalCachedMapCache> cacheMap;

  public CacheProviderImpl() {
    log.info("CacheProvider.new()");
  }

  /**
   * application scope init.
   *
   * @return
   */
  @PostConstruct
  public void init() {
    log.info("CacheProvider.init()");

    try {
      clientInitLock.writeLock().lock();
      clientConfig = new Config();

      String address = "redis://redis:6379"; // String.format("redis://%s:%d", config.getCacheHost(), config.getCachePort());

      log.debug("connecting to redis on {} with key {}", 
        address,
        config.getCacheLicenseKey());

      clientConfig.setRegistrationKey(config.getCacheLicenseKey());
      clientConfig
          .useSingleServer()
          .setAddress(address); // String.format("redis://%s:%d", config.getCacheHost(), config.getCachePort()));

      client = Redisson.create(clientConfig);

      clientVersion = new AtomicInteger(client.hashCode());
      prevClientVersion = new AtomicInteger(0);

      cacheConfigMap = new ConcurrentHashMap<>();
      cacheMap = new ConcurrentHashMap<>();
    } finally {
      clientInitLock.writeLock().unlock();
    }
  }

  /** refresh cache client. */
  private void refreshClient() {
    log.info("CacheProvider.refreshClient()");
    int localClientVersion = clientVersion.get();
    try {
      clientInitLock.writeLock().lock();

      // will only refresh client if the clientVersion != prevClientVersion
      // this is only to prevent double initialization when things failed.
      if (localClientVersion != prevClientVersion.get()) {
        if (Objects.nonNull(client)) {
          if (!(client.isShutdown() || client.isShuttingDown())) {
            client.shutdown(1000L, 1000L, TimeUnit.MILLISECONDS);
          }
        }

        client = Redisson.create(clientConfig);
        prevClientVersion.set(clientVersion.get());
        clientVersion.set(client.hashCode());
      }
    } finally {
      clientInitLock.writeLock().unlock();
    }
  }

  /**
   * refresh client and refresh cache.
   *
   * @return
   */
  @Override
  public void refresh() {
    log.info("CacheProvider.refresh()");
    refreshClient();
    initCache();
  }

  /** @return */
  private RedissonClient getClient() {
    try {
      clientInitLock.readLock().lock();
      return this.client;
    } finally {
      clientInitLock.readLock().unlock();
    }
  }

  /**
   * initialized cache
   *
   * @param cacheName
   * @param kType
   * @param vType
   * @param size
   * @param ttl
   */
  @Override
  public void initCache(String cacheName, Class kType, Class vType, Integer size, Integer ttl) {
    if (this.cacheConfigMap.containsKey(cacheName)) {
      throw new ApplicationException(
          "CacheProvider.initCache(cacheName, kType, vType, size, ttl) => [%s] already exist, won't be initialized",
          cacheName);
    }

    CacheConfig cacheConfig = new CacheConfig(cacheName, size, ttl, kType, vType);

    try {
      cacheLock.writeLock().lock();
      this.cacheConfigMap.put(cacheName, cacheConfig);
    } finally {
      cacheLock.writeLock().unlock();
    }

    initCache(cacheConfig);
  }

  /** initialize all cache. */
  private void initCache() {
    List<CacheConfig> configs = new ArrayList<>();
    try {
      cacheLock.readLock().lock();
      for (Map.Entry<String, CacheConfig> entry : cacheConfigMap.entrySet()) {
        configs.add(entry.getValue());
      }
    } finally {
      cacheLock.readLock().unlock();
    }

    for (CacheConfig config : configs) {
      initCache(config);
    }
  }

  /**
   * individual initialize cache.
   *
   * @param cacheConfig
   */
  private void initCache(CacheConfig cacheConfig) {
    if (Objects.isNull(cacheConfig)) {
      throw new ApplicationException(
          "CacheProvider.initCache(cacheConfig) => [cacheConfig] is null");
    }

    RLocalCachedMapCache cache =
        initCache(
            cacheConfig.getKType(),
            cacheConfig.getVType(),
            cacheConfig.getCacheName(),
            cacheConfig.getSize(),
            cacheConfig.getTtl());

    try {
      clientInitLock.writeLock().lock();
      cacheMap.put(cacheConfig.getCacheName(), cache);
    } finally {
      clientInitLock.writeLock().unlock();
    }
  }

  /**
   * @param keyType
   * @param valueType
   * @param cacheName
   * @param size
   * @param ttl
   * @param <K>
   * @param <V>
   * @return
   */
  private <K, V> RLocalCachedMapCache<K, V> initCache(
      Class<K> keyType, Class<V> valueType, String cacheName, int size, int ttl) {
    RedissonClient client = getClient();
    LocalCachedMapOptions options =
        LocalCachedMapOptions.defaults()
            .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LFU)
            .cacheSize(size)
            .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
            .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)
            .timeToLive(ttl, TimeUnit.MINUTES)
            .maxIdle(ttl, TimeUnit.MINUTES);
    return client.getLocalCachedMapCache(cacheName, options);
  }

  /**
   * @param cacheName
   * @param key
   * @param value
   */
  @Override
  public void put(String cacheName, Object key, Object value) {
    if (Objects.isNull(key)) {
      throw new ApplicationException("CacheProvider.put(cacheName, key, value) => [key] is null");
    }
    if (Objects.isNull(value)) {
      throw new ApplicationException("CacheProvider.put(cacheName, key, value) => [value] is null");
    }
    CacheConfig cacheConfig = getCacheConfig(cacheName);

    if (!key.getClass().isAssignableFrom(cacheConfig.getKType())) {
      throw new ApplicationException(
          "CacheProvider.put(cacheName, key, value) => [%s] is not of type [%s]",
          key.getClass(), cacheConfig.getKType());
    }
    if (!value.getClass().isAssignableFrom(cacheConfig.getVType())) {
      throw new ApplicationException(
          "CacheProvider.put(cacheName, key, value) => [%s] is not of type [%s]",
          value.getClass(), cacheConfig.getVType());
    }

    RLocalCachedMapCache cache = getCache(cacheName);
    executeSafe(
        () -> {
          cache.fastPut(key, value, cacheConfig.getTtl(), TimeUnit.MINUTES);
        });
  }

  /**
   * @param cacheName
   * @param key
   */
  @Override
  public void remove(String cacheName, Object key) {
    if (Objects.isNull(key)) {
      throw new ApplicationException("[key] is null");
    }
    CacheConfig cacheConfig = getCacheConfig(cacheName);
    if (!key.getClass().isAssignableFrom(cacheConfig.getKType())) {
      throw new ApplicationException(
          "CacheProvider.remove(cacheName, key) => [%s] is not of type [%s]",
          key.getClass(), cacheConfig.getKType());
    }

    RLocalCachedMapCache cache = getCache(cacheName);

    executeSafe(
        () -> {
          executeSafe(() -> cache.remove(key));
        });
  }

  /** @param cacheName */
  @Override
  public void removeAll(String cacheName) {
    RLocalCachedMapCache cache = getCache(cacheName);
    executeSafe(
        () -> {
          executeSafe(() -> cache.clear());
        });
  }

  /**
   * @param cacheName
   * @param valueType
   * @param key
   * @param <V>
   * @return
   */
  @Override
  public <V> V get(String cacheName, Class<V> valueType, Object key) {
    if (Objects.isNull(key)) {
      throw new ApplicationException("[key] is null");
    }
    CacheConfig cacheConfig = getCacheConfig(cacheName);
    if (!key.getClass().isAssignableFrom(cacheConfig.getKType())) {
      throw new ApplicationException(
          "CacheProvider.get(cacheName, valueType, key) => [%s] is not of type [%s]",
          key.getClass(), cacheConfig.getKType());
    }
    if (valueType != cacheConfig.getVType()) {
      throw new ApplicationException(
          "CacheProvider.get(cacheName, valueType, key) => [%s] is not of type [%s]",
          valueType, cacheConfig.getVType());
    }

    RLocalCachedMapCache cache = getCache(cacheName);
    Object value = querySafe(() -> cache.get(key));

    if (Objects.isNull(value)) {
      return null;
    }

    if (value.getClass().isAssignableFrom(valueType)) {
      return (V) value;
    } else {
      throw new ApplicationException(
          "CacheProvider.get(cacheName, valueType, key) => [%s] is not of type [%s]",
          value.getClass(), valueType);
    }
  }

  /**
   * get cache config base on cache name.
   *
   * @param cacheName
   * @return
   */
  private CacheConfig getCacheConfig(String cacheName) {
    if (CommonUtils.isWhitespaceOrNull(cacheName)) {

      throw new ApplicationException(
          "CacheProvider.getCacheConfig(cacheName) => [%s] is null or empty", cacheName);
    }

    try {
      this.cacheLock.readLock().lock();
      if (!this.cacheConfigMap.containsKey(cacheName)) {
        throw new ApplicationException(
            "CacheProvider.getCacheConfig(cacheName) => [%s] unable to find cache config",
            cacheName);
      }
      return this.cacheConfigMap.get(cacheName);
    } finally {
      this.cacheLock.readLock().unlock();
    }
  }

  /**
   * get cache base on cache name.
   *
   * @param cacheName cache names
   * @return
   */
  private RLocalCachedMapCache getCache(String cacheName) {
    if (CommonUtils.isWhitespaceOrNull(cacheName)) {
      throw new ApplicationException(
          "CacheProvider.getCache(cacheName) => [%s] is null or empty", cacheName);
    }

    try {
      this.cacheLock.readLock().lock();
      if (!this.cacheMap.containsKey(cacheName)) {
        throw new ApplicationException(
            "CacheProvider.getCache(cacheName) => [%s] unable to find cache", cacheName);
      }
      return this.cacheMap.get(cacheName);
    } finally {
      this.cacheLock.readLock().unlock();
    }
  }

  /**
   * wrapper command to execute the query, just to ensure that we had unify handler of error
   *
   * @param cmd
   */
  protected void executeSafe(Command cmd) {
    try {
      cmd.execute();
    } catch (RedisConnectionException ce) {
      log.warn("cannot connect to redis cache server, {}", ce.getMessage());
    } catch (RedisTimeoutException te) {
      log.warn("connect to redis server timeout, {}", te.getMessage());
      // recreate client and cache
      try {
        refresh();
        log.info("recreated caches");
      } catch (Exception re) {
        log.warn("failed recreate redission client or caches {}", re.getMessage());
      }
    }
  }

  /**
   * wrapper query to execute the query, just to ensure that we had unify handler of error
   *
   * @param query
   * @param <V>
   * @return
   */
  protected <V> V querySafe(Query<V> query) {
    try {
      return query.query();
    } catch (RedisConnectionException ce) {
      log.warn("cannot connect to redis cache server, {}", ce.getMessage());
    } catch (RedisTimeoutException te) {
      log.warn("connect to redis server timeout, {}", te.getMessage());
      // recreate client and cache
      try {
        refresh();
        log.info("recreated caches");
      } catch (Exception re) {
        log.warn("failed recreate redission client or caches {}", re.getMessage());
      }
    }
    return null;
  }

  /** wrapper interface for command. */
  protected interface Command {
    void execute();
  }

  /**
   * wrapper interface for query.
   *
   * @param <V>
   */
  protected interface Query<V> {
    V query();
  }

  /** Cache config */
  @Getter
  @AllArgsConstructor
  public static final class CacheConfig<K, V> {
    private String cacheName;
    private Integer size;
    private Integer ttl;
    private Class<K> kType;
    private Class<V> vType;
  }
}
