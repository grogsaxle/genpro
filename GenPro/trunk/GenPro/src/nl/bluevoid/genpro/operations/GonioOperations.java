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

import nl.bluevoid.genpro.cell.LibraryCell;

public class GonioOperations {
  public static final LibraryCell GONIO_OPS = new LibraryCell(GonioOperations.class);

  public static double surfaceFromRadius(double r) {
    return Math.PI * r * r;
  }

  public static double surfaceFromDiameter(double d) {
    return surfaceFromRadius(d / 2);
  }

  public static double surfaceElipseFromDiameter(double w, double h) {
    return Math.PI * (w / 2) * (h / 2);
  }

  public static double volumeElipseFromDiameter(double w, double h, double z) {
    return  Math.PI * w * h * z* (4.0 / 3.0);
  }

}
