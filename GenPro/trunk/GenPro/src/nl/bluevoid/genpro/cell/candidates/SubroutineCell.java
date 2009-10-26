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

import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.cell.Calculable;
import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.util.XMLBuilder;


public class SubroutineCell extends Cell implements Calculable {

  public SubroutineCell(String name) {
    super(name, CellTypeEnum.SubroutineCell);
  }

  /*
   * (non-javadoc)
   */
  //private Calculable[] steps;

  public void calc() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("calc is not implemented yet");
  }

  public void connectCell(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) {
    // TODO Auto-generated method stub
    throw new IllegalStateException("connectCell is not implemented yet");
  }

  public void mutate(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) {
    // TODO Auto-generated method stub
    throw new IllegalStateException("mutate is not implemented yet");
  }

  public void restoreConnections(CellMap map) {
    // TODO Auto-generated method stub
    throw new IllegalStateException("restoreConnections is not implemented yet");
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

  public void resetIsUsedForOutput() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("resetIsUsedForOutput is not implemented yet");
  }

  //@Override  //from interface, does only work in 1.6 and up
  public void validateLeadsToInputCell() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("validateLeadsToInputCell is not implemented yet");
  }

  //@Override  //from interface, does only work in 1.6 and up
  public void setCascadeUsedForOutput() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("setCascadeUsedForOutput is not implemented yet");
  }

  //@Override  //from interface, does only work in 1.6 and up
  public int getCalced() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("getCalced is not implemented yet");
  }

  //@Override  //from interface, does only work in 1.6 and up
  public int getErrored() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("getErrored is not implemented yet");
  }

  //@Override  //from interface, does only work in 1.6 and up
  public void resetCallAndErrorCounter() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("resetCallAndErrorCounter is not implemented yet");
  }
}
