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

package nl.bluevoid.genpro.cell;

@SuppressWarnings("serial")
public class NoCellOfTypeFoundException extends NoCellFoundException {

  public NoCellOfTypeFoundException() {
    super();
  }

  public NoCellOfTypeFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoCellOfTypeFoundException(String string) {
    super(string);
  }

  public NoCellOfTypeFoundException(Throwable cause) {
    super(cause);
  }
}
