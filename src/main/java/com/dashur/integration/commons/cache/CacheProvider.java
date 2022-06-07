package com.dashur.integration.commons.cache;

/** Cache provider, including initialise of cache. and basic operations of cache. */
public interface CacheProvider {
  /** @return re-initilized cache provider and return refreshed all registered cache. */
  void refresh();

  /**
   * register and initlized cache.
   *
   * @param cacheName
   * @param kType
   * @param vType
   * @param size
   * @param ttl
   */
  void initCache(String cacheName, Class kType, Class vType, Integer size, Integer ttl);

  /**
   * @param cacheName
   * @param key
   * @param value
   */
  void put(String cacheName, Object key, Object value);

  /**
   * @param cacheName
   * @param key
   */
  void remove(String cacheName, Object key);

  /** @param cacheName */
  void removeAll(String cacheName);

  /**
   * @param cacheName
   * @param valueType
   * @param key
   * @param <V>
   * @return
   */
  <V> V get(String cacheName, Class<V> valueType, Object key);
}
