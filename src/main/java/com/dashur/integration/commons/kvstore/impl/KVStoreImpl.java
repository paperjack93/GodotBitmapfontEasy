package com.dashur.integration.commons.kvstore.impl;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.kvstore.KVStore;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Singleton
public class KVStoreImpl implements KVStore {
  @Inject CommonsConfig config;

  private JedisPool jedisPool;

  @PostConstruct
  public void init() {
    jedisPool = new JedisPool(buildPoolConfig(), config.getKvHost(), config.getKvPort());
  }

  private JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  Jedis jedis() {
    return jedisPool.getResource();
  }

  @Override
  public void put(String key, String value) {
    try (Jedis jedis = jedis()) {
      jedis.set(key, value);
    }
  }

  @Override
  public void putWithTtl(String key, Long ttl, String value) {
    put(key, value);
    try (Jedis jedis = jedis()) {
      jedis.expire(key, ((Long) (ttl / 1000L)).intValue());
    }
  }

  @Override
  public Long delete(String key) {
    try (Jedis jedis = jedis()) {
      if (jedis.exists(key)) {
        return jedis.del(key);
      }
    }

    return 0L;
  }

  @Override
  public String get(String key) {
    try (Jedis jedis = jedis()) {
      if (jedis.exists(key)) {
        return jedis.get(key);
      }
    }

    throw new EntityNotExistException("KVStoreImpl.get(key) => [%s] - not exists", key);
  }

  @Override
  public Optional<String> getOrEmpty(String key) {
    try {
      return Optional.of(get(key));
    } catch (EntityNotExistException e) {
      return Optional.empty();
    }
  }

  @Override
  public Long setsAdd(String key, String... members) {
    try (Jedis jedis = jedis()) {
      return jedis.sadd(key, members);
    }
  }

  @Override
  public Long setsAddWithTtl(String key, Long ttl, String... members) {
    Long counts = setsAdd(key, members);
    try (Jedis jedis = jedis()) {
      jedis.expire(key, ((Long) (ttl / 1000L)).intValue());
    }

    return counts;
  }

  @Override
  public Long setsRemove(String key, String... members) {
    try (Jedis jedis = jedis()) {
      if (jedis.exists(key)) {
        return jedis.srem(key, members);
      }
    }
    // return 0L if not exist.
    return 0L;
  }

  @Override
  public Long setsCount(String key) {
    try (Jedis jedis = jedis()) {
      if (jedis.exists(key)) {
        return jedis.scard(key);
      }
    }
    // return 0 if not exists.
    return 0L;
  }

  @Override
  public Boolean setsExists(String key, String member) {
    try (Jedis jedis = jedis()) {
      if (jedis.exists(key)) {
        return jedis.sismember(key, member);
      }
    }
    // return false if not exists
    return Boolean.FALSE;
  }

  @Override
  public Set<String> setsMembers(String key) {
    try (Jedis jedis = jedis()) {
      if (jedis.exists(key)) {
        return jedis.smembers(key);
      }
    }
    // return empty hashsets if not exists
    return new HashSet<>();
  }
}
