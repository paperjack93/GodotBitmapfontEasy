package com.dashur.integration.commons.kvstore;

import java.util.Optional;
import java.util.Set;

/** Simple kv store. */
public interface KVStore {
  /**
   * @param key
   * @param ttl in millis
   * @param value
   */
  void putWithTtl(String key, Long ttl, String value);

  /**
   * @param key
   * @param value
   */
  void put(String key, String value);

  /** @param key */
  Long delete(String key);

  /**
   * @param key
   * @return
   */
  String get(String key);

  /**
   * @param key
   * @return
   */
  Optional<String> getOrEmpty(String key);

  /**
   * @param key
   * @param members
   * @return no of members added
   */
  Long setsAdd(String key, String... members);

  /**
   * @param key
   * @param ttl
   * @param members
   * @return no of members added
   */
  Long setsAddWithTtl(String key, Long ttl, String... members);

  /**
   * @param key
   * @param members
   * @return no of members removed
   */
  Long setsRemove(String key, String... members);

  /**
   * @param key
   * @return no of members
   */
  Long setsCount(String key);

  /**
   * @param key
   * @param member
   * @return
   */
  Boolean setsExists(String key, String member);

  /**
   * @param key
   * @return
   */
  Set<String> setsMembers(String key);
}
