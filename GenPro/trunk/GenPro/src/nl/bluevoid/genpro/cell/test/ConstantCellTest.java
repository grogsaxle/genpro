/*
 * This file is part of GenPro, Reflective Object Oriented Genetic Programming.
 *
 * GenPro offers a dual license model containing the GPL (GNU General Public License) version 2  
 * as well as a commercial license.
 *
 * For licensing information please see the file license.txt included with GenPro
 * or have a look at the top of class nl.bluevoid.genpro.cell.Cell which representatively
 * includes the GenPro license policy applicable for any file delivered with GenPro.
 */

package nl.bluevoid.genpro.cell.test;

import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.util.Debug;
import junit.framework.Assert;
import junit.framework.TestCase;

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
