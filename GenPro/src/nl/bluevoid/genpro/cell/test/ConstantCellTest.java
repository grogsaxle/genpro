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

package nl.bluevoid.genpro.cell.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.util.Debug;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class ConstantCellTest extends TestCase {

  public void testClone() {
    ConstantCell c1 = new ConstantCell("naam", Double.class, -100, 100);
    ConstantCell copy = c1.clone();
    Assert.assertEquals(c1.getName(), copy.getName());
    Assert.assertEquals(c1.getValue(), copy.getValue());
    Assert.assertEquals(c1.getValueType(), copy.getValueType());
    Assert.assertEquals(c1.getMin(), copy.getMin());
    Assert.assertEquals(c1.getMax(), copy.getMax());
    Assert.assertEquals(c1.getRange(), copy.getRange());
  }

  public void testMutate() {
    ConstantCell c1 = new ConstantCell("naam", Double.class, -100, 100);
    double d1 = (Double) c1.getValue();
    c1.mutate();
    double d2 = (Double) c1.getValue();
    Assert.assertTrue(Math.abs(d1 - d2) > 0.0000001);
  }

  public void testMutate2() {
    for (int i = 0; i < 5000; i++) {
      ConstantCell c1 = new ConstantCell("naam", Integer.class, -100, 100);
      Debug.checkRange((Integer) c1.getValue(), -100, 100);
      //int d1 = (Integer) c1.getValue();
      c1.mutate();
      //int d2 = (Integer) c1.getValue();
      //Assert.assertTrue(Math.abs(d1 - d2) > 0.0000001);
      Debug.checkRange((Integer) c1.getValue(), -100, 100);
    }
  }
}
