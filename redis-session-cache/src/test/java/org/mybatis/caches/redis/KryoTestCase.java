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

import org.bidtime.session.cache.redis.KryoSerializer;
import org.junit.Test;
import org.mybatis.caches.JUnitTestBase;
import org.mybatis.caches.bean.Student;

/**
 * Test with Ubuntu
 * sudo apt-get install redis-server
 * execute the test
 */
public final class KryoTestCase extends JUnitTestBase {
  
  //@Autowired
  //private RedisCacherTransfer redisCacherTransfer;
  
  //private static final String KEY = KryoTestCase.class.getName();

  @Test
  public void serial() {
    Student u = Student.newStudent();
    System.out.println("u:" + u);
    byte[] bytes = KryoSerializer.INSTANCE.serialize(u);
    System.out.println("bytes:" + bytes);
    Student b = (Student)KryoSerializer.INSTANCE.unserialize(bytes);
    System.out.println("b:" + b);
  }

  @Test
  public void unserial() {
  }

}
