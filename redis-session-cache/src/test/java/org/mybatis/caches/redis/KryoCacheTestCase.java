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

import java.util.List;

import org.bidtime.session.cache.redis.UserRedisCache;
import org.junit.Test;
import org.mybatis.caches.bean.Student;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test with Ubuntu
 * sudo apt-get install redis-server
 * execute the test
 */
public final class KryoCacheTestCase extends RedisTestCase {
  
  @Autowired
  private UserRedisCache cache;
  
  private static final String KEY_BEAN = "student";
  private static final String KEY_LIST = "list";

  @Test
  public void serialBean() {
    Student u = Student.newStudent();
    System.out.println("u:" + u);
    cache.set(KEY_BEAN, u);
    System.out.println("put: " + KEY_BEAN + ", ( " + u + " ) ");
  }

  @Test
  public void unserialBean() {
    Student b = (Student)cache.get(KEY_BEAN);
    System.out.println("b:" + b);
    System.out.println("get: " + KEY_BEAN + " = " + b);
  }

  @Test
  public void serialList() {
    List<Student> u = Student.newList();
    System.out.println("u:" + u);
    cache.set(KEY_LIST, u);
    System.out.println("put: " + KEY_LIST + ", ( " + u + " ) ");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void unserialList() {
    List<Student> b = (List<Student>)cache.get(KEY_LIST);
    System.out.println("b:" + b);
    System.out.println("get: " + KEY_LIST + " = " + b);
  }

}
