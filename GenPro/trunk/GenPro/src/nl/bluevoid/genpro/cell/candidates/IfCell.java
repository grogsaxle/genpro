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

  @Override
  public void setCascadeUsedForOutput() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("setCascadeUsedForOutput is not implemented yet");
  }

  @Override
  public int getCalced() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("getCalced is not implemented yet");
  }

  @Override
  public int getErrored() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("getErrored is not implemented yet");
  }

  @Override
  public void resetCallAndErrorCounter() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("resetCallAndErrorCounter is not implemented yet");
  }
}
