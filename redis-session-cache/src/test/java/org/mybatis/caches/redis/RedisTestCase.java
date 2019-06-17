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
package org.mybatis.caches.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.bidtime.session.cache.redis.UserRedisCache;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mybatis.caches.JUnitTestBase;

/**
 * Test with Ubuntu
 * sudo apt-get install redis-server
 * execute the test
 */
public class RedisTestCase extends JUnitTestBase {

  protected static String DEFAULT_ID = RedisTestCase.class.getName();

  protected static UserRedisCache cache;

  @BeforeClass
  public static void newCache() {
    cache = new UserRedisCache(DEFAULT_ID);
  }

  @Test
  public void shouldDemonstrateCopiesAreEqual() {
    for (int i = 0; i < 1000; i++) {
      cache.set(i, i);
      //assertEquals(i, cache.get(i));
    }
  }

  @Test
  public void shouldRemoveItemOnDemand() {
    cache.set(0, 0);
    assertNotNull(cache.get(0));
    cache.del(0);
    assertNull(cache.get(0));
  }

  @Test
  public void shouldFlushAllItemsOnDemand() {
    for (int i = 0; i < 5; i++) {
      cache.set(i, i);
    }
    assertNotNull(cache.get(0));
    assertNotNull(cache.get(4));
    //cache.clear();
    assertNull(cache.get(0));
    assertNull(cache.get(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateCache() {
    //cache = new RedisCaches((String)null);
  }

  @Test
  public void shouldVerifyCacheId() {
    //assertEquals("REDIS", cache.getId());
  }

  @Test
  public void shouldVerifyToString() {
    assertEquals("Redis {REDIS}", cache.toString());
  }

  @Test
  public void shouldDeleteExpiredCache() throws Exception {
    // set timeout to 3 secs
    cache.setTimeout(3);
    cache.set(0, 0);
    Thread.sleep(2000);
    cache.set(1, 1);
    // 2 secs : not expired yet
    //assertEquals(0, cache.get(0));
    Thread.sleep(2000);
    // 4 secs : should be expired
    assertNull(cache.get(0));
    assertNull(cache.get(1));
    // Make sure timeout is re-set
    cache.set(2, 2);
    Thread.sleep(2000);
    // 2 secs : not expired yet
    cache.set(3, 3);
    //assertEquals(2, cache.get(2));
    Thread.sleep(2000);
    // 4 secs : should be expired
    assertNull(cache.get(2));
    assertNull(cache.get(3));
  }
  
}
