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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.GridExecutionError;
import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.ReflectUtil;
import nl.bluevoid.genpro.util.Sneak;
import nl.bluevoid.genpro.util.StringUtil;
import nl.bluevoid.genpro.util.XMLBuilder;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
/**
 * @author robbio
 * 
 */
public class CallCell extends ValueCell implements Calculable {

  private static final Object[] nullParams = new Object[0];
  /*
   * (non-javadoc)
   */
  protected Method targetMethod;

  // protected boolean[] paramIsPrimitive;

  protected boolean isStaticCall;

  protected ValueCell targetCell;

  protected ValueCell[] params;

  protected Object[] paramObjects = nullParams;
  private int calced = 0;
  private int errored = 0;

  public CallCell(String name, Class<?> valueType) {
    super(name, valueType, false, CellTypeEnum.CallCell);
  }

  public static CallCell getInstanceFromXML(String XML) {

    return null;
  }

  public void getXML(XMLBuilder x) {
    x.startTag(getClass().getName());
    x.add("name", getName());
    x.add("type", getValueType().getName());
    x.add("target", getTargetCell().getName());
    x.add("method", getTargetMethod().getName());
    for (ValueCell c : params) {
      x.add("param", c.getName());
    }
    x.endTag();
  }

  public final void calc() throws IllegalAccessException, InvocationTargetException, GridExecutionError {
    // clear value!!!!
    value = null;

    calced++;
    if (params.length > 0) {
      for (int i = 0; i < paramObjects.length; i++) {
        paramObjects[i] = params[i].getValue();
      }
    }
    try {
      if (isStaticCall) {
        setValue(targetMethod.invoke(null, paramObjects));
      } else if (targetCell.getValue() == null) {
        // TODO it is null, what to do, nothing??
        throw new GridExecutionError(null);
      } else {
        setValue(targetMethod.invoke(targetCell.getValue(), paramObjects));
      }
    } catch (GridExecutionError g) {
      throw g;
    } catch (RuntimeException nup) {
      errored++;
      // consume!!!! a param was null, arrayindex out of bouunds etc, no problem go on
    } catch (Throwable e) {
      errored++;
      if (!(e.getCause() instanceof RuntimeException)) {
        System.err.println("targetCell.getValue() " + targetCell.getValue());
        System.err.println("targetMethod " + targetMethod);
        System.err.println("paramObjects " + StringUtil.join("\n", paramObjects));
        Sneak.sneakyThrow(e);
      }
    }
    // Debug.println("invoking:" + targetMethod + "on object:" + target + " from cell:" + targetCell);
  }

  /**
   * Getter of the property <tt>targetMethod</tt>
   * 
   * @return Returns the targetMethod.
   * 
   */
  public Method getTargetMethod() {
    return targetMethod;
  }

  /**
   * Getter of the property <tt>targetCell</tt>
   * 
   * @return Returns the targetCell.
   * 
   */
  public ValueCell getTargetCell() {
    return targetCell;
  }

  public ValueCell[] getParams() {
    return params;
  }

  public void setParams(final ValueCell[] params) {
    Debug.checkNotNull(params, "params");
    this.params = params;
    paramObjects = new Object[params.length];
  }

  public void setTargetMethod(final Method targetMethod) {
    Debug.checkNotNull(targetMethod, "targetMethod");
    this.targetMethod = targetMethod;
    // setIsStatic
    isStaticCall = Modifier.isStatic(targetMethod.getModifiers());
  }

  public void setTargetCell(final ValueCell targetCell) {
    Debug.checkNotNull(targetCell, "targetCell");
    this.targetCell = targetCell;
    // if (targetCell.isLeadsToInputCell()) {
    // setLeadsToInputCell(true);
    // }
  }

  @Override
  public String toString() {
    String method = (targetMethod == null) ? "null" : targetMethod.getName();
    String returnType = (targetMethod == null) ? "null" : targetMethod.getReturnType().getSimpleName();
    String targetClass = (targetCell == null) ? "null" : targetCell.getValueType().getSimpleName();
    String targetCellStr = (targetCell == null) ? "null" : targetCell.getClass().getSimpleName() + " "
        + targetCell.getName() + " nr" + targetCell.getSerialNr();
    String paramsStr = " ";
    if (params != null) {
      for (ValueCell cell : params) {
        paramsStr += cell.getValueType().getSimpleName() + ":" + cell.getName() + " nr" + cell.getSerialNr()
            + " ";
      }
    }
    return super.toString() + " calls " + targetClass + "." + method + "(" + paramsStr + ") : " + returnType
        + " on " + targetCellStr;
  }

  // @Override
  public void restoreConnections(final CellMap map) throws NoCellFoundException {
    // replace params
    for (int i = 0; i < params.length; i++) {
      if (!(params[i].getCellType() == CellTypeEnum.LibraryCell))
        params[i] = map.getByNameOrValueType(params[i]);
    }
    // replace targetcell
    if (!(targetCell.getCellType() == CellTypeEnum.LibraryCell))
      targetCell = map.getByNameOrValueType(targetCell);
  }

  public void validateLeadsToInputCell() {
    setLeadsToInputCell(false);
    if (targetCell.isLeadsToInputCell()) {
      setLeadsToInputCell(true);
      return;
    }
    for (ValueCell param : params) {
      if (param.isLeadsToInputCell()) {
        setLeadsToInputCell(true);
        return;
      }
    }
  }

  // @Override
  public void setCascadeUsedForOutput() {
    if (!isUsedForOutput()) {
      // Debug.println("setUsedForOutput called on "+this);
      super.setUsedForOutput(true);
      if (targetCell != null) {
        ValueCell.setCascadeUsedForOutput(targetCell);
        for (ValueCell param : params) {
          ValueCell.setCascadeUsedForOutput(param);
        }
      }
    }
  }

  public void mutate(final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      final ArrayList<ValueCell> allParamCells) {
    // mutate param or target?
    if (hasParams() && random.nextBoolean()) {
      try {
        mutateParam(allParamCells);
        // mutatedParams++;
      } catch (NoCellFoundException e) {
        // no solution?? it was connected so 1 solution should be there!!
        e.printStackTrace();
      }
    } else {
      try {
        connectCell(callTargetsByReturnType, allParamCells);
      } catch (NoCellFoundException e) {
       Debug.printErrln("Mutation failed "+e.getMessage());
      }
    }
  }

  private void mutateParam(final ArrayList<ValueCell> paramTargets) throws NoCellFoundException {
    final int paramNr = random.nextInt(getParams().length);
    final Class<?> type = getParams()[paramNr].getValueType();
    getParams()[paramNr] = Util.getRandomCell(ReflectUtil.getClassForPrimitive(type), paramTargets);
  }

  @Override
  public CallCell clone() {
    final CallCell clone = (CallCell) super.clone();
    clone.calced = 0;
    clone.errored = 0;
    clone.params = new ValueCell[params.length];
    System.arraycopy(params, 0, clone.params, 0, params.length);
    return clone;
  }

  public boolean hasParams() {
    return params.length > 0;
  }

  public void connectCell(final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      final ArrayList<ValueCell> allParamCells) throws NoCellFoundException {
    boolean found = false;
    int count = 0;
    final Class<?> returnType = getValueType();
    // get all methods that have the right return type
    final ArrayList<CallTarget> methods = callTargetsByReturnType.get(returnType);
    if (methods == null) {
      Debug.println("Error connecting cell:" + this);
      System.err.flush();
      Util.printCallTargets(callTargetsByReturnType);
      System.out.flush();

      throw new IllegalArgumentException("no methods found to connect to for:" + returnType + " from " + this);
    }
    // Debug.println("cell"+this);

    // Debug.println("returnType:" + returnType.getSimpleName());
    // for (CallTarget callTarget : methods) {
    // Debug.println("callTarget:" + callTarget);
    // }
    if (methods.size() == 0) {
      throw new IllegalStateException("no calls deliver a method for return type:" + returnType);
    }

    NoCellFoundException noCellFoundException = null;
    while (count < 300 && !found) {
      count++;
      try {
        // get random method
        final CallTarget ct = methods.get(random.nextInt(methods.size()));
        // Debug.println("trying:" + ct);
        // linkup needed parameters for the method
        final ValueCell[] paramCells = Util.getRandomParamsCells(ct, allParamCells);

        // we found a connection, so hook it up!
        setTargetCell(ct.cell);
        setTargetMethod(ct.method);
        // System.out.println(foundMethod);
        setParams(paramCells);
        found = true;
      } catch (NoCellFoundException e) {
        noCellFoundException = e;
        // Debug.println("no cell found!");
      }
    }

    if (!found) {
      noCellFoundException.addInfo("no solution after " + count + " tries for cell:" + this);
     throw noCellFoundException;
    }
    validateLeadsToInputCell();
  }

  public boolean canMutate() {
    return true;
  }

  public int getCalced() {
    return calced;
  }

  public int getErrored() {
    return errored;
  }

  // @Override
  public void resetCallAndErrorCounter() {
    errored = 0;
    calced = 0;
  }

}
