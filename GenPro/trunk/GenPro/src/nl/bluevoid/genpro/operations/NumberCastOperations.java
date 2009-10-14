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

package nl.bluevoid.genpro.operations;

import nl.bluevoid.genpro.NoCallTarget;

public class NumberCastOperations {

  public static Integer castInteger(Double d) {
    return d.intValue();
  }

  public static Byte castByte(Double d) {
    return d.byteValue();
  }

  public static Short castShort(Double d) {
    return d.shortValue();
  }

  @NoCallTarget
  public static String getJavaSyntax(String methodName) {
    if (methodName.startsWith("cast")) {
      return "(" + methodName.substring(4) + ")";
    }
    throw new IllegalArgumentException("No javaSyntax for:" + methodName);
  }
}
