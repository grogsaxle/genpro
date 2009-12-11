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

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.util.GewogenKansSelector;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class GewogenKansSelectorTest extends TestCase {

  public void testThis() {
    GewogenKansSelector<String> gks = new GewogenKansSelector<String>(new Setup("GewogenKansSelector test"));
    gks.add("10", 10, 1);
    gks.add("50", 50, 5);
    gks.add("40", 40, 4);
    
    int found50 = 0;
    int found10 = 0;
    int found40 = 0;

    for (int i = 0; i < 100000; i++) {
      String item = gks.getRandomItem();
      if (item.equals("50")) {
        found50++;
      } else if (item.equals("10")) {
        found10++;
      } else if (item.equals("40")) {
        found40++;
      } else {
        throw new IllegalStateException("found something weird:" + item);
      }
    }
    System.out.println("found50 " + found50);
    System.out.println("found10 " + found10);
    System.out.println("found40 " + found40);
    Assert.assertTrue(found50 > 49000);
    Assert.assertTrue(found50 < 51000);
  }
}
