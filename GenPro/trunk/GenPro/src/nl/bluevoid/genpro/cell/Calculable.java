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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.GridExecutionError;

public interface Calculable extends CellInterface{

  /**
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws GridExecutionError 
   * 
   */
  public void calc() throws IllegalAccessException, InvocationTargetException, GridExecutionError;

  /**
   * "override" van Cell om deze ook op calculable aanwezig te laten zijn. 
   * @return
   * @throws NoCellFoundException 
   */  
  public void connectCell(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) throws NoCellFoundException;
  
  public void restoreConnections(CellMap map) throws NoCellFoundException;
  
  public void mutate(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells);
  
  public void setCascadeUsedForOutput();
  
  public void validateLeadsToInputCell();

  public void resetCallAndErrorCounter();
  
  public int getCalced() ;
  
  public int getErrored();
}
