/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.bluevoid.genpro.cell;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.GridExecutionError;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
  
  public boolean isLeadsToInputCell();

  public void resetCallAndErrorCounter();
  
  public int getCalced() ;
  
  public int getErrored();
}
