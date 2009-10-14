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

public class Stopwatch {
  private long startTime;
  private final String action;

  Stopwatch(String action) {
    this.action = action;
    reset();
  }

  public void reset() {
    startTime = System.currentTimeMillis();
  }

  public double ellapsedSeconds() // return seconden als double
  {
    return (System.currentTimeMillis() - startTime) / 1000.0;
  }

  public void printEllapsedTime() {
    System.out.println(action + " took " + ellapsedSeconds());
  }

}
