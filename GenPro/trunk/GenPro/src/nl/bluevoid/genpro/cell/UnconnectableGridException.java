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
public class UnconnectableGridException extends NoCellFoundException {

  public UnconnectableGridException() {
    super();
  }

  public UnconnectableGridException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnconnectableGridException(String string) {
    super(string);
  }

  public UnconnectableGridException(Throwable cause) {
    super(cause);
  }

}
