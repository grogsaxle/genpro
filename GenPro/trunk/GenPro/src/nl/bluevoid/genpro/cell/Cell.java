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

import java.util.Random;

import nl.bluevoid.genpro.util.Debug;

public abstract class Cell implements Cloneable, CellInterface{

  private static int serialNrCounter=1;
  
  protected static final Random random=new Random(System.currentTimeMillis());
  private final String name;
  private int serialNr;
  private final CellTypeEnum cellType;
  
  public Thread calcThread;
  
  public Cell(String name, CellTypeEnum cellType) {
    this.cellType = cellType;
    Debug.checkNotNull(name, "name");
    this.name = name;
    serialNr=serialNrCounter++;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return "'"+name+"'";
  }
  
  public synchronized Cell clone() {
    try {
      final Cell c=(Cell)super.clone();
      c.serialNr=serialNrCounter++;
      return c;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      throw new IllegalStateException();
    }
  }

  /* (non-Javadoc)
   * @see nl.bluevoid.genpro.cell.CellInterface#getSerialNr()
   */
  public int getSerialNr() {
    return serialNr;
  }

  /* (non-Javadoc)
   * @see nl.bluevoid.genpro.cell.CellInterface#getCellType()
   */
  public CellTypeEnum getCellType() {
    return cellType;
  }
}
