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

/**
 * @author Rob van der Veer
 * @since 1.0
 */
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

  public boolean isLeadsToInputCell() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("isLeadsToInputCell is not implemented yet");
  }
}
