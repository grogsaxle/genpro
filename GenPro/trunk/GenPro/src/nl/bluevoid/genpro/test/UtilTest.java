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

package nl.bluevoid.genpro.test;

import junit.framework.TestCase;
import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.util.Debug;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class UtilTest extends TestCase {
  public void testMutateConst() {
    //checkrange
    for (int i = 0; i < 200; i++) {
      double val = Util.mutateperc(100.0, 5, 0, 300);
      Debug.checkRange(val, 95, 105);
      assertTrue(val <= 105);
      assertTrue(val >= 95);
      //System.out.println(val);
    }
    //check lower bound
    for (int i = 0; i < 200; i++) {
      double val = Util.mutateperc(100.0, 5, 99, 300);
      Debug.checkRange(val, 99, 105.1);
      assertTrue(val < 105.1);
      assertTrue(val >= 99);
    }
    //check upper bound
    for (int i = 0; i < 200; i++) {
      double val = Util.mutateperc(100.0, 5, 0, 102);
      Debug.checkRange(val, 95, 102);
      assertTrue(val <= 102);
      assertTrue(val >= 95);
      //System.out.println(val);
    }
  }
}
