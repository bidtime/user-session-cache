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
package org.bidtime.session.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public final class HashMapCache implements IUserCache {

  private static final Logger log = LoggerFactory.getLogger(HashMapCache.class);
  
  private final Map<Object, Object> map = new ConcurrentHashMap<Object, Object>();
  
  public static final HashMapCache single = new HashMapCache();

  private Integer timeout;                    // seconds，保留, 暂时不用

  public HashMapCache() {
  }

  @SuppressWarnings("unchecked")
  public <T> T set(final Object k, T v) {
    T t = (T)this.map.put(k, v);
    log.debug("set: {}, {}, tm({})", k, t, timeout);
    return t;
  }

  @SuppressWarnings("unchecked")
  public <T> T get(final Object k) {
    T t = (T)this.map.get(k);
    log.debug("get: {}, {}, tm({})", k, t, timeout);
    return t;
  }

  @SuppressWarnings("unchecked")
  public <T> T del(final Object k) {
    T t = (T)this.map.get(k);
    log.debug("del: {}, {}, tm({})", k, t, timeout);
    return t;
  }
  
  public Integer getTimeout() {
    return timeout;
  }
  
  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

}