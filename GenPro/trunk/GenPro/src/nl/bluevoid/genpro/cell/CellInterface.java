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

import nl.bluevoid.genpro.util.XMLBuilder;

public interface CellInterface {

  public abstract boolean canMutate();
  
  public abstract String getName();

  public abstract int getSerialNr();

  public boolean isUsedForOutput();
  
  public abstract CellTypeEnum getCellType();

  public abstract Object clone();
  
  public abstract void getXML(XMLBuilder x);

  public String toString();
}
