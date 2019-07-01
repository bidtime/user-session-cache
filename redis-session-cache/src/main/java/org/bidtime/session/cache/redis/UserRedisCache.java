/**
 *    Copyright 2015-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.bidtime.session.cache.redis;

import java.util.Collection;

import org.bidtime.session.cache.IUserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

/**
 * 本源码摘自 mybatis-redis 项目，并加以改造
 * 
 * 项目 mybatis-redis 地址：https://github.com/mybatis/redis-cache.git
 * 
 * Cache adapter for Redis.
 *
 * @author riverbo
 * @since 2018.04.17
 */
public final class UserRedisCache implements IUserCache {

  private static final Logger log = LoggerFactory.getLogger(UserRedisCache.class);

  private String id;
  
  private byte[] idBytes;

  private Integer timeout;                    // seconds

  public UserRedisCache(final String id) {
    if (id == null) {
      throw new IllegalArgumentException("Cache instances require an ID");
    }
    this.id = id;
    this.idBytes = id.getBytes();
    if (log.isDebugEnabled()) {
      log.debug("cache: {}", id);
    }
  }

  public UserRedisCache(final String id, Integer timeout) {
    this(id);
    this.timeout = timeout;
  }

  public UserRedisCache(Integer timeout) {
    this("");
    this.timeout = timeout;
  }

  public UserRedisCache() {
    this("");
    this.timeout = 60 * 60 * 60;
  }

  /**
   * 
   * @param callback
   * @return
   * @author riverbo
   * @since 2018.07.17
   */
  private <T> T execute(RedisCallback<T> callback) {
    return UserRedisCacherTransfer.execute(callback);
  }

  public boolean lock(final Object key) {
    return lock(key, true);
  }
  
  public boolean lock(final Object key, Integer timeout) {
    return lock(key, true, timeout);
  }
  
  public boolean lock(final Object key, final Object value) {
    return lock(key, value, this.timeout);
  }
  
  /**
   * 
   * @param key
   * @param value
   * @return
   * @author riverbo
   * @since 2018.07.17
   */
  public boolean lock(final Object key, final Object value, Integer timeout) {
    return execute(new RedisCallback<Boolean>() {

      @SuppressWarnings("rawtypes")
      @Override
      public Boolean doInRedis(RedisConnection conn) {
        Boolean success = null;
        final byte[] keyBytes = key.toString().getBytes();
        byte[] dataBytes = null;
        if (value != null) {
          dataBytes = UserRedisCacherTransfer.serialize(value);            
        }
        success = conn.hSetNX(idBytes, keyBytes, dataBytes);
        if (timeout != null && conn.ttl(idBytes) == -1) {
          conn.expire(idBytes, timeout);
        }
        if (log.isDebugEnabled()) {
          if (value != null) {
            if (value instanceof Collection) {
              log.debug("hSetNX: {}-{}, {}:size({}), tm({})", id, key, success, ((Collection)value).size(), timeout);
            } else {
              log.debug("hSetNX: {}-{}, {}, tm({})", id, key, success, timeout);
            }
          } else {
            log.debug("hSetNX: {}-{}, {}, tm({})", id, key, success, timeout);
          }
        }
        return success;
      }
    });
  }
  
  public boolean inc(final String key, final Long max) {
    return this.inc(key, max, this.timeout);
  }
  
  public boolean inc(final String key, final Long max, int timeout) {
    return execute(new RedisCallback<Boolean>() {

      @Override
      public Boolean doInRedis(RedisConnection conn) {
        Boolean success = null;
        final byte[] idKey = (id + key).toString().getBytes();
        Long lrst = conn.incr(idKey);
        if (lrst == 1) {                // this is new
          conn.expire(idKey, timeout);
        }
        log.debug("inc: {}_{}, {}, tm({})", id, key, success, timeout);        
        if (lrst >= max) {
          success = false;
        } else {
          success = true;
        }
        return success;
      }
    });
  }

  /**
   * 
   * @param key
   * @return
   * @author riverbo
   * @since 2018.07.17
   */
  public boolean unlock(final Object key) {
    Object l = del(key);
    return (l != null) ? true : false;
  }

  @Override
  public <T> T set(final Object k, T v) throws RuntimeException {
    return execute(new RedisCallback<T>() {

      @SuppressWarnings("rawtypes")
      @Override
      public T doInRedis(RedisConnection conn) {
        Boolean success = null;
        final byte[] keyBytes = k.toString().getBytes();
        byte[] dataBytes = null;
        if (v != null) {
          dataBytes = UserRedisCacherTransfer.serialize(v);            
        }
        success = conn.hSet(idBytes, keyBytes, dataBytes);
        if (timeout != null && conn.ttl(idBytes) == -1) {
          conn.expire(idBytes, timeout);
        }
        if (log.isDebugEnabled()) {
          if (v != null) {
            if (v instanceof Collection) {
              log.debug("hSet: {}-{}, {}:size({}), tm({})", id, k, success, ((Collection)v).size(), timeout);
            } else {
              log.debug("hSet: {}-{}, {}, tm({})", id, k, success, timeout);
            }
          } else {
            log.debug("hSet: {}-{}, {}, tm({})", id, k, success, timeout);
          }
        }
        return null;
      }
    });
  }

  @Override
  public <T> T get(final Object k) throws RuntimeException {
    return get(k, false);
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(final Object k, boolean del) throws RuntimeException {
    return execute(new RedisCallback<T>() {

      @SuppressWarnings("rawtypes")
      @Override
      public T doInRedis(RedisConnection conn) {
        final byte[] keyBytes = k.toString().getBytes();
        T t = null;
        final byte[] dataBytes = conn.hGet(idBytes, keyBytes);
        if (dataBytes != null) {
          t = (T)UserRedisCacherTransfer.unserialize(dataBytes);
        }
        if (log.isDebugEnabled()) {
          if (t != null) {
            if (t instanceof Collection) {
              log.debug("hGet: {}-{}, {}:size({})", id, k, t.getClass().getSimpleName(), ((Collection)t).size());
            } else {
              log.debug("hGet: {}-{}, {}", id, k, t.getClass().getSimpleName());
            }
          } else {
            log.debug("hGet: {}-{}, {}: is null", id, k, (t == null) ? null : t.getClass().getSimpleName());
          }
        }
        if (del) {
          Long l = conn.hDel(idBytes, keyBytes);
          if (log.isDebugEnabled()) {
            log.debug("hDel: {}-{}, {}", id, k, (l == null) ? null : l);
          }        
        }
        return t;
      }
    });
  }

  @Override
  public <T> T del(final Object k) throws RuntimeException {
    execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection conn) {
        final byte[] keyBytes = k.toString().getBytes();
        Long l = conn.hDel(idBytes, keyBytes);
        if (log.isDebugEnabled()) {
          log.debug("hDel: {}-{}, {}", id, k, (l == null) ? null : l);
        }        
        return l;
      }
    });
    return null;
  }

  @Override
  public String toString() {
    return "Redis {" + id + "}";
  }

  // properties
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public Integer getTimeout() {
    return timeout;
  }

}