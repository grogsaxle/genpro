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

package nl.bluevoid.genpro.test;

import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.util.Debug;
import junit.framework.TestCase;

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
