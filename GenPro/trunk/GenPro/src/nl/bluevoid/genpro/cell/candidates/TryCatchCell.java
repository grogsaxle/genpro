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

package nl.bluevoid.genpro.cell.candidates;

import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.util.XMLBuilder;


public class TryCatchCell extends Cell {
  //private int nrOfSteps;
  //private Class<?> exceptionType;//????
 // private Calculable[] onError; 
  
  public TryCatchCell(String name) {
    super(name, CellTypeEnum.TryCatchCell);
  }

  public boolean canMutate() {
    return true;
  }

  public void getXML(XMLBuilder x) {
    // TODO Auto-generated method stub
    throw new IllegalStateException("getXML is not implemented yet");
  }

  public boolean isUsedForOutput() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("isUsedForOutput is not implemented yet");
  }
}
