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

import nl.bluevoid.genpro.util.InfoException;

@SuppressWarnings("serial")
public class NoCellFoundException extends InfoException {

  public NoCellFoundException(String message, Throwable cause) {
    super(message, cause); 
  }

  public NoCellFoundException(String string) {
    super(string);
  }

  public NoCellFoundException(Throwable cause) {
    super(cause);
  }

  public NoCellFoundException() {
  }
}
