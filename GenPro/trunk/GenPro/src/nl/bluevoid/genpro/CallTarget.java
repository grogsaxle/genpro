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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.bluevoid.genpro.cell.ValueCell;
/**
 * 
 * @author Rob van der Veer
 * @since 0.5
 */
public class CallTarget {
  public final ValueCell cell;
  public final Method method;

  public CallTarget(ValueCell cell, Method method) {
    this.cell = cell;
    this.method = method;
  }

  @Override
  public String toString() {
    String staticStr = Modifier.isStatic(method.getModifiers()) ? "static " : "";
    return cell.getClass().getSimpleName() + " " + cell.getName() + " - " +staticStr+ cell.getValueType().getSimpleName()
        + "." + method.getName() + "(...)";
  }
}
