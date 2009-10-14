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

package nl.bluevoid.genpro.util;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.Setup;

public class GewogenKansSelectorTest extends TestCase {
  

  public void testThis() {
    GewogenKansSelector<String> gks = new GewogenKansSelector<String>(new Setup("GewogenKansSelector test"));
    gks.add("10", 10, 1);
    gks.add("50", 50, 5);
    gks.add("40", 20, 4);

    int found50 = 0;
    for (int i = 0; i < 100000; i++) {
      if (gks.getRandomItem().equals("50")) {
        found50++;
      }
    }
    System.out.println("found50 "+found50);
    Assert.assertTrue(found50 > 49000);
    Assert.assertTrue(found50 < 51000);
  }
}
