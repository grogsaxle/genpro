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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.operations.BlackList;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.ReflectUtil;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class ValueCell extends Cell implements Cloneable {
  protected final Class<?> valueType;

  protected Object value;

  private final boolean staticOnly;

  protected final boolean valueIsNumber;

  /**
   * this boolean tells whether the cell is (in)directly connected to some input. Any output should be
   * connected with a cell which has leadsToInputCell set to true, otherwise the grid does not work!
   */
  private boolean leadsToInputCell = false;

  /**
   * this boolean tells whether the cell is used for calculating some output, if not calc does not to have
   * been called
   */

  private boolean usedForOutput = false;

  private static final HashMap<Class<?>, ArrayList<Method>> methodCacheInstance = new HashMap<Class<?>, ArrayList<Method>>(
      100);
  private static final HashMap<Class<?>, ArrayList<Method>> methodCacheStatic = new HashMap<Class<?>, ArrayList<Method>>(
      100);

  public ValueCell(final String name, final Class<?> valueType, final boolean staticOnly,
      final CellTypeEnum cellType) {
    super(name, cellType);
    Debug.checkNotNull(valueType, "valueType");
    this.valueType = valueType;
    this.staticOnly = staticOnly;
    valueIsNumber = Number.class.isAssignableFrom(valueType);
  }

  public ValueCell(final String name, final Class<?> valueType, final Object value, final CellTypeEnum cellType) {
    this(name, valueType, false, cellType);
    Debug.checkNotNull(value, "value");
    this.value = value;
  }

  public ArrayList<Method> getAllMethods() {
    ArrayList<Method> meths = null;
    // select correct cache (static / instance)
    HashMap<Class<?>, ArrayList<Method>> cache = isStaticOnly() ? methodCacheStatic : methodCacheInstance;
    meths = cache.get(valueType);
    if (meths == null) {
      ArrayList<Method> meths2 = ReflectUtil.getAllMethods(valueType, isStaticOnly());
      Debug.println("getAllMethods called for:" + valueType + " staticOnly:" + staticOnly + " found methods:"
          + meths2.size());
      meths = BlackList.filterBlackListed(valueType, meths2);
      cache.put(valueType, meths);
      if (meths.size() != meths2.size()) {
        Debug.println("Remaining methods for:" + valueType + " staticOnly:" + staticOnly + " methods:"
            + meths.size());
      }
      for (final Method method : meths) {
        System.out.println("  " + method);
      }
    }
    return meths;
  }

  private static final HashMap<Class<?>, HashMap<Class<?>, ArrayList<Method>>> methodsPerClassPerReturnType = new HashMap<Class<?>, HashMap<Class<?>, ArrayList<Method>>>();
  private static final HashMap<Class<?>, HashMap<Class<?>, ArrayList<Method>>> staticMethodsPerClassPerReturnType = new HashMap<Class<?>, HashMap<Class<?>, ArrayList<Method>>>();

  public HashMap<Class<?>, ArrayList<Method>> getAllMethodsByReturnType() {
    HashMap<Class<?>, HashMap<Class<?>, ArrayList<Method>>> cache = isStaticOnly() ? staticMethodsPerClassPerReturnType
        : methodsPerClassPerReturnType;
    HashMap<Class<?>, ArrayList<Method>> methodsByReturnType = cache.get(valueType);
    if (methodsByReturnType == null) {
      // create entry for this Class
      methodsByReturnType = new HashMap<Class<?>, ArrayList<Method>>();
      Debug.println("creating methodsCache for Class " + valueType);
      cache.put(valueType, methodsByReturnType);
      for (final Method m : getAllMethods()) {
        final Class<?> returnType = ReflectUtil.getClassForPrimitive(m.getReturnType());
        ArrayList<Method> methods = methodsByReturnType.get(returnType);
        if (methods == null) {
          // create entry for this returntype
          methods = new ArrayList<Method>();
          methodsByReturnType.put(returnType, methods);
        }
        methods.add(m);
      }
    }
    return methodsByReturnType;
  }

  // is ietsje sneller!
  public final void addCallTarget2(final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType) {
    for (final Method m : getAllMethods()) {
      // zet deze meteen om bij het opvragen
      Class<?> ret = ReflectUtil.getClassForPrimitive(m.getReturnType());
      ArrayList<CallTarget> arr = callTargetsByReturnType.get(ret);
      if (arr == null) {
        arr = new ArrayList<CallTarget>();
        callTargetsByReturnType.put(ret, arr);
      }
      // store calltargets in cache of hoe selectie beter doen??
      final CallTarget c = new CallTarget(this, m);
      arr.add(c);
    }
  }

//  public final void addCallTarget3(final HashMap<Class, ArrayList<CallTarget>> callTargetsByReturnType) {
//    final HashMap<Class, ArrayList<Method>> cache = getAllMethodsByReturnType();
//    for (final Class returnType : cache.keySet()) {
//      // zet deze meteen om bij het opvragen
//      ArrayList<CallTarget> arr = callTargetsByReturnType.get(returnType);
//      if (arr == null) {
//        arr = new ArrayList<CallTarget>();
//        callTargetsByReturnType.put(returnType, arr);
//      }
//      for (Method m : cache.get(returnType)) {
//        final CallTarget c = new CallTarget(this, m);
//        arr.add(c);
//      }
//    }
//  }

  public Class<?> getValueType() {
    return valueType;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(final Object value) {
    if (value == null || valueType.isAssignableFrom(value.getClass())) {
      this.value = value;
    } else {
      throw new IllegalArgumentException("Expected object of type:" + valueType.getSimpleName() + " but got:"
          + value.getClass().getSimpleName());
    }
  }

  public String toString() {
    String clazz = (valueType == null) ? "null" : valueType.getSimpleName() + ".class";
    return getClass().getSimpleName() + " " + super.toString() + " - content/obj: " + clazz + "/ "
        + ((value == null) ? "null" : value) + " usedForOutput:" + isUsedForOutput();
  }

  public boolean isStaticOnly() {
    return staticOnly;
  }

  public boolean isLeadsToInputCell() {
    return leadsToInputCell;
  }

  public boolean isUsedForOutput() {
    return usedForOutput;
  }

  public boolean isValueA_Number() {
    return valueIsNumber;
  }

  public ValueCell clone() {
    ValueCell cell = (ValueCell) super.clone();
    cell.usedForOutput = false;
    return cell;
  }

  public void setLeadsToInputCell(final boolean leadsToInputCell) {
    this.leadsToInputCell = leadsToInputCell;
  }

  public void setUsedForOutput(boolean b) {
    usedForOutput = b;
  }

  public static void setCascadeUsedForOutput(ValueCell cell){
    if(cell.isUsedForOutput()) return;
    if(cell instanceof Calculable){
      ((Calculable)cell).setCascadeUsedForOutput();
    }
    else{
      cell.setUsedForOutput(true);
    }
  }
  
//  public void resetIsUsedForOutput() {
//    usedForOutput = false;
//  }
}
