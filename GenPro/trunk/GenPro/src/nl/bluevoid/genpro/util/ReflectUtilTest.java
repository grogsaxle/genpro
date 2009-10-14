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

import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.TestCase;

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
