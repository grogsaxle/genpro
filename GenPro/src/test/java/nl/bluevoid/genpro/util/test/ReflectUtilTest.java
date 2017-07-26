/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.bluevoid.genpro.util.test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.TestCase;
import nl.bluevoid.genpro.util.ReflectUtil;
import nl.bluevoid.genpro.util.Stopwatch;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class ReflectUtilTest extends TestCase {
  public void testGetMethods() throws Exception {
    ArrayList<Method> ms = ReflectUtil.getAllMethods(Math.class, true);
    for (Method method : ms) {
      System.out.println(method);
      System.out.println("returntype:" + method.getReturnType().getName());
    }
  }

  public void testCanCast() {
    Stopwatch st=new Stopwatch("100000 canCast");
    for (int i = 0; i < 100000; i++) {
      assertTrue(ReflectUtil.canCastNumber(Integer.class, Double.class));
      assertTrue(ReflectUtil.canCastNumber(Integer.class, Integer.class));
      assertFalse(ReflectUtil.canCastNumber(Double.class, Byte.class));
      assertFalse(ReflectUtil.canCastNumber(String.class, Byte.class));
    }
    st.printEllapsedTime();
  }
}
