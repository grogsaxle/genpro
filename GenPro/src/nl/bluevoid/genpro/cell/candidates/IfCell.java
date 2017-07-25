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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.GridExecutionError;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.cell.Calculable;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.util.XMLBuilder;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class IfCell extends Cell implements Calculable {

  private final ArrayList<CallCell> onTrueList = new ArrayList<CallCell>();
  private final ArrayList<CallCell> onFalseList = new ArrayList<CallCell>();
  private ValueCell booleanExpression;
  private ArrayList<ReferenceCell> values = new ArrayList<ReferenceCell>();

  public IfCell(String name, Setup setup) {
    super(name, CellTypeEnum.IfCell);

    Class<?> type = setup.getRandomCallCellType();
    ReferenceCell rc = new ReferenceCell("if1", type);
    values.add(rc);
    CallCell cellTrue = new CallCell("iffalse1", type);
    onTrueList.add(cellTrue);
    CallCell cellFalse = new CallCell("iftrue1", type);
    onFalseList.add(cellFalse);
  }

  public void calc() throws IllegalAccessException, InvocationTargetException, GridExecutionError {
    boolean value = ((Boolean) booleanExpression.getValue()).booleanValue();
    if (value) {
      for (int i = 0; i < getOnTrueList().size(); i++) {
        getOnTrueList().get(i).calc();
        values.get(i).setReferedCell(getOnTrueList().get(i));
        // Debug.println("if true ref "+i+"  "+values.get(i).toString());
      }
    } else {
      for (int i = 0; i < getOnTrueList().size(); i++) {
        getOnFalseList().get(i).calc();
        values.get(i).setReferedCell(getOnFalseList().get(i));
        // Debug.println("if false ref "+i+"  "+values.get(i).toString());
      }
    }
  }

  public ValueCell getBooleanExpression() {
    return booleanExpression;
  }

  public void setBooleanExpression(ValueCell booleanCell) {
    if (!booleanCell.getValueType().equals(Boolean.class)) {
      throw new IllegalArgumentException("Expected a cell with Boolean.class contenttype, instead we got"
          + booleanCell.toString());
    }
    this.booleanExpression = booleanCell;
  }

  public String toString() {
    String trueStr = "";
    for (Calculable cell : getOnTrueList()) {
      trueStr += cell.getName();
    }
    String falseStr = "";
    for (Calculable cell : getOnFalseList()) {
      falseStr += cell.getName();
    }
    return super.toString() + "  if( " + booleanExpression.getName() + " ) " + trueStr + " else " + falseStr;
  }

  public void connectCell(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) throws NoCellFoundException {

    setBooleanExpression(Util.getRandomCell(Boolean.class, allParamCells));

    for (int i = 0; i < onTrueList.size(); i++) {
      onTrueList.get(i).connectCell(callTargetsByReturnType, allParamCells);
    }
    for (int i = 0; i < onFalseList.size(); i++) {
      onFalseList.get(i).connectCell(callTargetsByReturnType, allParamCells);
    }
  }

  public void validateLeadsToInputCell() {
    throw new IllegalStateException("mutate is not implemented yet");
  }

  public void mutate(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) {
    throw new IllegalStateException("mutate is not implemented yet");
  }

  public void restoreConnections(CellMap map) {
    throw new IllegalStateException("restoreConnections is not implemented yet");
  }

  public ArrayList<ReferenceCell> getValueCells() {
    return values;
  }

  public ArrayList<CallCell> getOnTrueList() {
    return onTrueList;
  }

  public ArrayList<CallCell> getOnFalseList() {
    return onFalseList;
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
