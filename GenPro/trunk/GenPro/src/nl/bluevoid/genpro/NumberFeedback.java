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

package nl.bluevoid.genpro;

public class NumberFeedback {
  public static final int HIGHER = 123;
  public static final int LOWER = 345;
  
  public final int directive;
  public final double value;

  public NumberFeedback(int directive, double temperature) {
    super();
    this.directive = directive;
    this.value = temperature;
  }

  @Override
  public String toString() {
    String a = "" + value;
    switch (directive) {
    case HIGHER:
      return "> " + a;
    case LOWER:
      return "< " + a;
    default:
      throw new IllegalArgumentException();
    }
  }
}
